package com.example.tedebouna;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText idUsuario, idContra;
    private CheckBox checkBox;
    private Button btnIniciar, btnRegistrar, btnOlvide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referenciar los elementos del layout
        idUsuario = findViewById(R.id.idUsuario);
        idContra = findViewById(R.id.idContra);
        checkBox = findViewById(R.id.checkBox);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnRegistrar = findViewById(R.id.button3);
        btnOlvide = findViewById(R.id.button2);

        // Configurar el click listener para el botón de iniciar sesión
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarSesion();
            }
        });

        // Configurar el click listener para el botón de registrarse
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Registro.class));
            }
        });

        // Configurar el click listener para el botón de "Olvide mi contraseña"
        btnOlvide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implementar lógica para recuperar contraseña
            }
        });
    }

    private void iniciarSesion() {
        String correo = idUsuario.getText().toString().trim();
        String contra = idContra.getText().toString().trim();

        if (correo.isEmpty()) {
            idUsuario.setError("Correo es requerido");
            idUsuario.requestFocus();
            return;
        }

        if (contra.isEmpty()) {
            idContra.setError("Contraseña es requerida");
            idContra.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(correo, contra)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Inicio de sesión exitoso
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, menu_inferior.class));
                            finish();
                        }
                    } else {
                        // Si el inicio de sesión falla, mostrar un mensaje al usuario.
                        Toast.makeText(MainActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
