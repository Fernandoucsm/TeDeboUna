package com.example.tedebouna;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registro extends AppCompatActivity {

    private EditText editTextCorreo, editTextCreaContra, editTextConfirContra;
    private Button btnRegistrar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referenciar los elementos del layout
        editTextCorreo = findViewById(R.id.idCorreo);
        editTextCreaContra = findViewById(R.id.IdCreaContra);
        editTextConfirContra = findViewById(R.id.idConfirContra);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        // Configurar el click listener para el botón de registro
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarUsuario();
            }
        });
    }

    private void registrarUsuario() {
        String correo = editTextCorreo.getText().toString().trim();
        String creaContra = editTextCreaContra.getText().toString().trim();
        String confirContra = editTextConfirContra.getText().toString().trim();

        if (correo.isEmpty()) {
            editTextCorreo.setError("Correo es requerido");
            editTextCorreo.requestFocus();
            return;
        }

        if (creaContra.isEmpty()) {
            editTextCreaContra.setError("Contraseña es requerida");
            editTextCreaContra.requestFocus();
            return;
        }

        if (!creaContra.equals(confirContra)) {
            editTextConfirContra.setError("Las contraseñas no coinciden");
            editTextConfirContra.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(correo, creaContra)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registro exitoso
                            Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Registro.this, MainActivity.class));
                            finish();
                        } else {
                            // Si el registro falla, mostrar un mensaje al usuario.
                            Toast.makeText(Registro.this, "Error al registrar el usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("Registro", "Error al registrar el usuario", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Maneja el fallo aquí
                        Log.e("Registro", "Error al registrar el usuario", e);
                    }
                });
    }

    public void onClick(View view) {
        startActivity(new Intent(Registro.this, MainActivity.class));
    }
}
