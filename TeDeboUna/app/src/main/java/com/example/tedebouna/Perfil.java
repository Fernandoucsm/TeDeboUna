package com.example.tedebouna;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import androidx.fragment.app.FragmentTransaction;
import java.io.InputStream;
public class Perfil extends Fragment {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImageView;
    private boolean isImageSelected = false;
    private String profileImageUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.perfil, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();

            TextView nameTextView = view.findViewById(R.id.nameTextView);
            TextView emailTextView = view.findViewById(R.id.emailTextView);

            nameTextView.setText(name);
            emailTextView.setText(email);
        }

        profileImageView = view.findViewById(R.id.profileImageView);
        Button editImageButton = view.findViewById(R.id.editImageButton);
        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isImageSelected) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_IMAGE);
                }
            }
        });

        // Cargar la imagen de perfil desde Firestore y establecerla en profileImageView si no está cargada
        if (profileImageUrl == null) {
            loadProfileImage();
        } else {
            Picasso.get()
                    .load(profileImageUrl)
                    .placeholder(R.drawable.ayuda)
                    .error(R.drawable.logo1)
                    .into(profileImageView);
        }


        Button editSkillsButton = view.findViewById(R.id.editSkillsButton);
        editSkillsButton.setOnClickListener(v -> navigateToEditSkills());
        TextView skillsTextView = view.findViewById(R.id.skillsTextView);
        loadSkills(skillsTextView);
        return view;
    }

    private void loadProfileImage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    profileImageUrl = documentSnapshot.getString("profileImageUrl");
                    Log.d("Profile Image URL", profileImageUrl); // imprime la URL en el log
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Picasso.get()
                                .load(profileImageUrl)
                                .placeholder(R.drawable.ayuda)
                                .error(R.drawable.logo1)
                                .into(profileImageView);
                    }
                })
                .addOnFailureListener(e -> Log.e("Profile Image Error", "Error loading profile image", e));
    }
    private void navigateToEditSkills() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, new RegistrarHabilidades());
        transaction.addToBackStack(null);
        transaction.commit();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profileImageView.setImageBitmap(selectedImage);
                isImageSelected = true;

                // Subir la imagen a Firebase Storage
                uploadProfileImage(imageUri);
            } catch (Exception e) {
                Log.e("Profile Image Error", "Error processing selected image", e);
            }
        }
    }

    private void uploadProfileImage(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profileImagesRef = storageRef.child("profileImages/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
        profileImagesRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Obtener la URL de descarga y guardarla en Firestore
                    profileImagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        profileImageUrl = uri.toString();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .update("profileImageUrl", profileImageUrl);
                        Log.d("Download URL", "URL: " + profileImageUrl);
                    });
                    Log.d("Upload", "Image upload successful");
                })
                .addOnFailureListener(e -> Log.e("Upload", "Image upload failed", e));
    }
    private void loadSkills(TextView skillsTextView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    StringBuilder skills = new StringBuilder();
                    int i = 1;
                    while (documentSnapshot.contains("skill" + i)) {
                        String skill = documentSnapshot.getString("skill" + i);
                        skills.append(skill).append("\n");
                        i++;
                    }
                    skillsTextView.setText(skills.toString());
                })
                .addOnFailureListener(e -> Log.e("Skills Error", "Error loading skills", e));
    }
}
