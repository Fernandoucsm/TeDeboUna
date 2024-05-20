package com.example.tedebouna;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private FirebaseFirestore db;
    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.commentTextView.setText(comment.getContent());
        holder.commentUserNameTextView.setText(comment.getUserName());
        holder.commentUserEmailTextView.setText(comment.getUserEmail());

        // Obtén el ID del usuario actual
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Comprueba si el usuario actual es el autor del comentario
        if (currentUserId.equals(comment.getUserId())) {
            // Si el usuario actual es el autor del comentario, muestra el botón de borrar
            holder.deleteButton.setVisibility(View.VISIBLE);

            holder.deleteButton.setOnClickListener(v -> {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Comment commentToDelete = commentList.get(adapterPosition);

                    // Si el usuario actual es el autor del comentario, permite la acción de borrar
                    db.collection("comments").document(commentToDelete.getUserId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                commentList.remove(adapterPosition);
                                notifyItemRemoved(adapterPosition);
                            })
                            .addOnFailureListener(e -> Log.e("Delete Comment Error", "Error deleting comment", e));
                }
            });
        } else {
            // Si el usuario actual no es el autor del comentario, oculta el botón de borrar
            holder.deleteButton.setVisibility(View.GONE);
        }

        db.collection("users").document(comment.getUserId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Picasso.get()
                                .load(profileImageUrl)
                                .placeholder(R.drawable.ayuda)
                                .error(R.drawable.logo1)
                                .into(holder.commentUserProfileImageView);
                    }

                })
                .addOnFailureListener(e -> Log.e("Profile Image Error", "Error loading profile image", e));

    }
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentTextView;
        TextView commentUserNameTextView;
        TextView commentUserEmailTextView;
        de.hdodenhof.circleimageview.CircleImageView commentUserProfileImageView;
        Button deleteButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            commentUserNameTextView = itemView.findViewById(R.id.commentUserNameTextView);
            commentUserEmailTextView = itemView.findViewById(R.id.commentUserEmailTextView);
            commentUserProfileImageView = itemView.findViewById(R.id.commentUserProfileImageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}