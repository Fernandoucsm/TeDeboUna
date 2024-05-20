package com.example.tedebouna;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            commentUserNameTextView = itemView.findViewById(R.id.commentUserNameTextView);
            commentUserEmailTextView = itemView.findViewById(R.id.commentUserEmailTextView);
            commentUserProfileImageView = itemView.findViewById(R.id.commentUserProfileImageView);
        }
    }
}