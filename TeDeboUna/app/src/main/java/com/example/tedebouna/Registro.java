package com.example.tedebouna;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.auth.SignInMethodQueryResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;
import java.util.HashMap;


public class Registro extends AppCompatActivity {

    private EditText editTextNombre, editTextCorreo, editTextCreaContra, editTextConfirContra;
    private Button btnRegistrar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referenciar los elementos del layout
        editTextNombre = findViewById(R.id.idNombre);
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

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance();
    }

    private void registrarUsuario() {
        String nombre = editTextNombre.getText().toString().trim();
        String correo = editTextCorreo.getText().toString().trim();
        String creaContra = editTextCreaContra.getText().toString().trim();
        String confirContra = editTextConfirContra.getText().toString().trim();

        if (nombre.isEmpty()) {
            editTextNombre.setError("Nombre es requerido");
            editTextNombre.requestFocus();
            return;
        }

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

        mAuth.fetchSignInMethodsForEmail(correo)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            if (result.getSignInMethods().size() > 0) {
                                // El correo electrónico ya está en uso
                                Toast.makeText(Registro.this, "El correo electrónico ya está en uso", Toast.LENGTH_SHORT).show();
                            } else {
                                // El correo electrónico no está en uso, procede a registrar al usuario
                                mAuth.createUserWithEmailAndPassword(correo, creaContra)
                                        .addOnCompleteListener(Registro.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Registro exitoso
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                            .setDisplayName(nombre)
                                                            .build();

                                                    user.updateProfile(profileUpdates)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d("Registro", "Nombre de usuario actualizado.");
                                                                    }
                                                                }
                                                            });

                                                    // Guarda la información del usuario en Firestore
                                                    String userId = user.getUid();
                                                    Map<String, Object> userMap = new HashMap<>();
                                                    userMap.put("nombre", nombre);
                                                    userMap.put("correo", correo);

                                                    db.collection("users").document(userId)
                                                            .set(userMap)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(Registro.this, "Usuario registrado y datos guardados en Firestore.", Toast.LENGTH_SHORT).show();
                                                                        startActivity(new Intent(Registro.this, MainActivity.class));
                                                                        finish();
                                                                    } else {
                                                                        Toast.makeText(Registro.this, "Error al guardar datos en Firestore: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });

                                                } else {
                                                    // Si el registro falla, mostrar un mensaje al usuario.
                                                    Toast.makeText(Registro.this, "Error al registrar el usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    Log.e("Registro", "Error al registrar el usuario");
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
                        } else {
                            // Error al verificar el correo electrónico
                            Log.e("Registro", "Error al verificar el correo electrónico", task.getException());
                        }
                    }
                });
    }

    public void onClick(View view) {
        startActivity(new Intent(Registro.this, MainActivity.class));
    }
}
