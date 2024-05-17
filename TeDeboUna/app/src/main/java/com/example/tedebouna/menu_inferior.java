package com.example.tedebouna;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tedebouna.databinding.ActivityMenuInferiorBinding;

public class menu_inferior extends AppCompatActivity {

    ActivityMenuInferiorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuInferiorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new InicioFragment());

        binding.navigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navInicio) {
                replaceFragment(new InicioFragment());
            } else if (item.getItemId() == R.id.navNotificacion) {
                replaceFragment(new NotificacionFragment());
            } else if (item.getItemId() == R.id.navMas) {
                replaceFragment(new MasFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }
}
