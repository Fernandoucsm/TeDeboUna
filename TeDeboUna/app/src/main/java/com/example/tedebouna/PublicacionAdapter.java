package com.example.tedebouna;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.PublicacionViewHolder> {
    private List<Publicacion> listaPublicaciones;

    public PublicacionAdapter(List<Publicacion> listaPublicaciones) {
        this.listaPublicaciones = listaPublicaciones;
    }

    @NonNull
    @Override
    public PublicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publicacion, parent, false);
        return new PublicacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionViewHolder holder, int position) {
        Publicacion publicacion = listaPublicaciones.get(position);
        holder.bind(publicacion);
    }

    @Override
    public int getItemCount() {
        return listaPublicaciones.size();
    }

    static class PublicacionViewHolder extends RecyclerView.ViewHolder {
        // Declarar los botones
        Button likeButton;
        Button shareButton;

        public PublicacionViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializar los botones
            //  likeButton = itemView.findViewById(R.id.likeButton);
            //  shareButton = itemView.findViewById(R.id.shareButton);
        }

        public void bind(Publicacion publicacion) {
            // Configurar el listener de clic para el botón de "Dar like"
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aquí puedes implementar la lógica para "dar like" a la publicación
                }
            });

            // Configurar el listener de clic para el botón de "Compartir"
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aquí puedes implementar la lógica para compartir la publicación
                }
            });
        }
    }
}