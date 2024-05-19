package com.example.tedebouna;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class InicioFragment extends Fragment {

    private RecyclerView favorsRecyclerView;
    private List<Post> postList;
    private PostAdapter postAdapter;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        favorsRecyclerView = view.findViewById(R.id.favorsRecyclerView);
        FloatingActionButton addFavorButton = view.findViewById(R.id.addFavorButton);

        db = FirebaseFirestore.getInstance();
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        favorsRecyclerView.setAdapter(postAdapter);
        favorsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadPosts();

        addFavorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCreatePost();
            }
        });

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
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }

    private void navigateToCreatePost() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, new CrearPublicacionFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
