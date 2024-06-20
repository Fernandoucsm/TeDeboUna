package com.example.tedebouna;

import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private FirebaseFirestore db;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
        try {
            this.db = FirebaseFirestore.getInstance();
        } catch (Exception e) {
            Log.e("PostAdapter", "Error initializing Firestore", e);
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new PostViewHolder(view);
        } catch (Exception e) {
            Log.e("PostAdapter", "Error inflating layout", e);
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        try {
            Post post = postList.get(position);
            holder.postContentTextView.setText(post.getContent());
            holder.userEmailTextView.setText(post.getUserEmail());
            holder.userNameTextView.setText(post.getUserName());
            if (post.getImageUrl() != null) {
                holder.postImageView.setVisibility(View.VISIBLE);
                try {
                    Picasso.get().load(post.getImageUrl()).into(holder.postImageView);
                } catch (Exception e) {
                    Log.e("Picasso", "Error loading image", e);
                }

                // Aquí es donde agregas el OnClickListener para postImageView
                holder.postImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Deshabilitar el clic en la imagen
                        holder.postImageView.setClickable(false);

                        // Crear un AlertDialog.Builder
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());

                        // Crear un PhotoView para mostrar la imagen en tamaño completo con capacidades de zoom y paneo
                        PhotoView photoView = new PhotoView(holder.itemView.getContext());
                        try {
                            Picasso.get().load(post.getImageUrl()).into(photoView);
                        } catch (Exception e) {
                            Log.e("Picasso", "Error loading image into PhotoView", e);
                        }

                        // Permitir que la imagen se ajuste dentro del PhotoView manteniendo su relación de aspecto
                        photoView.setAdjustViewBounds(true);

                        // Configurar el AlertDialog.Builder
                        builder.setView(photoView);
                        builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        // Crear y mostrar el AlertDialog
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        // Ajustar el tamaño del AlertDialog para que coincida con el tamaño de la imagen
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        // Cuando se cierra el diálogo, habilitar de nuevo el clic en la imagen
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                holder.postImageView.setClickable(true);
                            }
                        });
                    }
                });
            } else {
                holder.postImageView.setVisibility(View.GONE);
            }

            try {
                db.collection("users").document(post.getUserId())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                            Log.d("Profile Image URL", profileImageUrl);
                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                Picasso.get()
                                        .load(profileImageUrl)
                                        .placeholder(R.drawable.ayuda)
                                        .error(R.drawable.logo1)
                                        .into(holder.userProfileImageView);
                            }
                        })
                        .addOnFailureListener(e -> Log.e("Profile Image Error", "Error loading profile image", e));
            } catch (Exception e) {
                Log.e("Firestore", "Error accessing Firestore", e);
            }

            holder.commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Make the comment section visible
                    holder.commentSection.setVisibility(View.VISIBLE);

                    // Obtén una referencia al documento post
                    DocumentReference postRef = db.collection("posts").document(post.getUserId());

                    // Crea una nueva colección en el documento post para los comentarios
                    CollectionReference commentsRef = postRef.collection("comments");

                    // Obtén los comentarios
                    commentsRef.get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                List<Comment> comments = queryDocumentSnapshots.toObjects(Comment.class);

                                // Set the adapter for the RecyclerView
                                holder.commentsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
                                holder.commentsRecyclerView.setAdapter(new CommentAdapter(comments));
                            })
                            .addOnFailureListener(e -> {
                                Log.w("Firestore", "Error obteniendo comentarios", e);
                            });
                }
            });

            holder.postCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comment = holder.newCommentEditText.getText().toString();
                    if (comment.isEmpty()) {
                        // Muestra un mensaje Toast si el EditText está vacío
                        Toast.makeText(holder.itemView.getContext(), "Por favor, escribe un comentario", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> commentData = new HashMap<>();
                    commentData.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    commentData.put("userName", FirebaseAuth.getInstance().getCurrentUser().getDisplayName()); // <-- Agregar el nombre de usuario
                    commentData.put("content", comment);

                    // Obtén una referencia al documento post
                    DocumentReference postRef = db.collection("posts").document(post.getUserId());

                    // Crea una nueva colección en el documento post para los comentarios
                    CollectionReference commentsRef = postRef.collection("comments");

                    // Agrega el nuevo comentario a la colección de comentarios
                    commentsRef.add(commentData)
                            .addOnSuccessListener(documentReference -> {
                                Log.d("Firestore", "Comentario agregado con ID: " + documentReference.getId());
                                holder.newCommentEditText.setText(""); // Limpia el EditText después de publicar el comentario

                                // Muestra un mensaje Toast cuando el comentario se agrega con éxito
                                Toast.makeText(holder.itemView.getContext(), "Comentario publicado con éxito", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.w("Firestore", "Error agregando comentario", e);

                                // Muestra un mensaje Toast cuando hay un error al agregar el comentario
                                Toast.makeText(holder.itemView.getContext(), "Error al publicar el comentario", Toast.LENGTH_SHORT).show();
                            });
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Cierra la sección de comentarios cuando se presiona fuera del post
                    holder.commentSection.setVisibility(View.GONE);
                }
            });

        } catch (Exception e) {
            Log.e("PostAdapter", "Error in onBindViewHolder", e);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postContentTextView;
        TextView userEmailTextView;
        TextView userNameTextView;
        ImageView postImageView;
        de.hdodenhof.circleimageview.CircleImageView userProfileImageView;
        Button commentButton;
        LinearLayout commentSection; // Asegúrate de que este campo esté definido
        EditText newCommentEditText;
        Button postCommentButton;
        RecyclerView commentsRecyclerView; // Add a field for the RecyclerView

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                postContentTextView = itemView.findViewById(R.id.postContentTextView);
                userEmailTextView = itemView.findViewById(R.id.userEmailTextView);
                userNameTextView = itemView.findViewById(R.id.userNameTextView);
                postImageView = itemView.findViewById(R.id.postImageView);
                userProfileImageView = itemView.findViewById(R.id.userProfileImageView);
                commentButton = itemView.findViewById(R.id.commentButton);
                commentSection = itemView.findViewById(R.id.commentSection); // Encuentra la sección de comentarios en tu layout
                newCommentEditText = itemView.findViewById(R.id.newCommentEditText);
                postCommentButton = itemView.findViewById(R.id.postCommentButton);
                commentsRecyclerView = itemView.findViewById(R.id.commentsRecyclerView); // Find the RecyclerView in your layout
            } catch (Exception e) {
                Log.e("PostAdapter", "Error finding views in ViewHolder", e);
            }
        }
    }
}
