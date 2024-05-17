package com.example.tedebouna;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        // Aquí se deben declarar las vistas que se van a usar en el item_publicacion

        public PublicacionViewHolder(@NonNull View itemView) {
            super(itemView);
            // Aquí se deben inicializar las vistas que se van a usar en el item_publicacion
        }

        public void bind(Publicacion publicacion) {
            // Aquí se deben setear los datos de la publicacion en las vistas
        }
    }
}