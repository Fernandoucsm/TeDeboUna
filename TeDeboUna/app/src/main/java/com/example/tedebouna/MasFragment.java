package com.example.tedebouna;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import static android.content.Context.MODE_PRIVATE;
public class MasFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mas, container, false);

        Button btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                // Redirige al usuario a MainActivity después de cerrar sesión
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();

                // Restablecer las preferencias compartidas
                getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                        .putBoolean("isRemembered", false)
                        .remove("email")
                        .remove("password")
                        .apply();
            }
        });

        Button btnProfile = view.findViewById(R.id.btn_profile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reemplaza el fragmento actual con Perfil cuando se haga clic en el botón
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, new Perfil());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        return view;
    }
}