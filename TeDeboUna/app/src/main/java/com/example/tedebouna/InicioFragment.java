package com.example.tedebouna;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InicioFragment extends Fragment {

    private static final String TAG = "InicioFragment";
    private RecyclerView favorsRecyclerView;
    private List<Post> postList;
    private PostAdapter postAdapter;
    private FirebaseFirestore db;
    private ImageView imageView;
    private String profileImageUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        favorsRecyclerView = view.findViewById(R.id.favorsRecyclerView);
        FloatingActionButton addFavorButton = view.findViewById(R.id.addFavorButton);
        imageView = view.findViewById(R.id.imageView);

        db = FirebaseFirestore.getInstance();
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        favorsRecyclerView.setAdapter(postAdapter);
        favorsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Cargar la imagen de perfil desde Firestore y establecerla en imageView si no está cargada
        if (profileImageUrl == null) {
            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        profileImageUrl = documentSnapshot.getString("profileImageUrl");
                        Log.d(TAG, "Profile Image URL: " + profileImageUrl);
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Picasso.get()
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.ayuda)
                                    .error(R.drawable.logo1)
                                    .into(imageView);
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error loading profile image", e));
        } else {
            Picasso.get()
                    .load(profileImageUrl)
                    .placeholder(R.drawable.ayuda)
                    .error(R.drawable.logo1)
                    .into(imageView);
        }

        loadPosts();
        addFavorButton.setOnClickListener(v -> navigateToCreatePost());

        imageView.setOnClickListener(v -> perfilUsuario());

        return view;
    }

    private void loadPosts() {
        db.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear();
                    postList.addAll(queryDocumentSnapshots.toObjects(Post.class));
                    postAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading posts", e));
    }

    private void navigateToCreatePost() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, new CrearPublicacionFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void perfilUsuario() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, new Perfil());
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
