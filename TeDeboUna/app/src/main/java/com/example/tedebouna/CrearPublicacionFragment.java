package com.example.tedebouna;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.*;
import java.util.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.storage.FirebaseStorage;


public class CrearPublicacionFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView selectedMediaImageView;
    private EditText postContentEditText;
    private Uri imageUri;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crear_publicacion, container, false);

        postContentEditText = view.findViewById(R.id.postContentEditText);
        selectedMediaImageView = view.findViewById(R.id.selectedMediaImageView);
        progressBar = view.findViewById(R.id.progressBar);
        Button addMediaButton = view.findViewById(R.id.addMediaButton);
        Button publishButton = view.findViewById(R.id.publishButton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        addMediaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishPost();
            }
        });

        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            selectedMediaImageView.setImageURI(imageUri);
            selectedMediaImageView.setVisibility(View.VISIBLE);
        }
    }

    private void publishPost() {
        final String postContent = postContentEditText.getText().toString().trim();

        if (postContent.isEmpty()) {
            Toast.makeText(getActivity(), "Por favor, ingresa algo para publicar", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        if (imageUri != null) {
            final StorageReference storageRef = storage.getReference().child("images/" + System.currentTimeMillis() + ".jpg");
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    savePostToFirestore(postContent, uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            savePostToFirestore(postContent, null);
        }
    }

    private void savePostToFirestore(String postContent, String imageUrl) {
        // Obtener el correo electrónico del usuario actual
        String userEmail = mAuth.getCurrentUser().getEmail();

        // Crear un objeto Map para almacenar los datos de la publicación
        Map<String, Object> post = new HashMap<>();
        post.put("content", postContent);
        post.put("imageUrl", imageUrl);
        post.put("userId", mAuth.getCurrentUser().getUid());
        post.put("userEmail", userEmail); // Agregar el correo electrónico del usuario
        post.put("timestamp", System.currentTimeMillis());

        // Guardar la publicación en Firestore
        db.collection("posts")
                .add(post)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Publicación exitosa
                            Toast.makeText(getActivity(), "Publicación exitosa", Toast.LENGTH_SHORT).show();
                            // Limpiar los campos después de publicar
                            postContentEditText.setText("");
                            selectedMediaImageView.setImageURI(null);
                            selectedMediaImageView.setVisibility(View.GONE);
                            imageUri = null;
                        } else {
                            // Error al publicar la publicación
                            Toast.makeText(getActivity(), "Error al publicar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}