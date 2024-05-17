package com.example.tedebouna;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class InicioFragment extends Fragment {
    private RecyclerView recyclerView;
    private PublicacionAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        recyclerView = view.findViewById(R.id.favorsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Crear datos de prueba
        List<Publicacion> listaPublicaciones = new ArrayList<>();
        // Agregar publicaciones a la lista...

        adapter = new PublicacionAdapter(listaPublicaciones);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addFavorButton = view.findViewById(R.id.addFavorButton);
        addFavorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reemplazar el fragmento actual con CrearPublicacionFragment
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, new CrearPublicacionFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}