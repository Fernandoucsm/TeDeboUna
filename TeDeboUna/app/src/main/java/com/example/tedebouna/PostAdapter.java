package com.example.tedebouna;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.postContentTextView.setText(post.getContent());
        holder.userEmailTextView.setText(post.getUserEmail()); // Mostrar el correo electrónico del usuario
        if (post.getImageUrl() != null) {
            holder.postImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(post.getImageUrl()).into(holder.postImageView);
        } else {
            holder.postImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postContentTextView;
        TextView userEmailTextView; // Nuevo TextView para mostrar el correo electrónico del usuario
        ImageView postImageView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postContentTextView = itemView.findViewById(R.id.postContentTextView);
            userEmailTextView = itemView.findViewById(R.id.userEmailTextView); // Asignar el TextView del correo electrónico del usuario
            postImageView = itemView.findViewById(R.id.postImageView);
        }
    }
}