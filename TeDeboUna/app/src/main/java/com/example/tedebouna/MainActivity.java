package com.example.tedebouna;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import android.content.DialogInterface;
import android.app.AlertDialog;
import androidx.annotation.NonNull;
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

        // Verificar si el usuario ha marcado la opción "Recordar contraseña"
        SharedPreferences preferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        checkBox.setChecked(preferences.getBoolean("isRemembered", false));
        if (checkBox.isChecked()) {
            idUsuario.setText(preferences.getString("email", ""));
            idContra.setText(preferences.getString("password", ""));
        }
        // Configurar el OnEditorActionListener para el campo de texto de la contraseña
        idContra.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // Aquí se llama al método que se ejecuta cuando se presiona el botón de iniciar sesión
                    iniciarSesion();
                    return true;
                }
                return false;
            }
        });

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
                // Crear un AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                // Configurar el título y el mensaje del AlertDialog
                builder.setTitle("Restablecer contraseña");
                builder.setMessage("Por favor, introduce tu correo electrónico para enviar el enlace de restablecimiento de contraseña.");

                // Configurar un EditText en el AlertDialog para que el usuario pueda introducir su correo electrónico
                final EditText input = new EditText(MainActivity.this);
                builder.setView(input);

                // Configurar los botones del AlertDialog
                builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = input.getText().toString().trim();
                        if (!email.isEmpty()) {
                            // Enviar el correo electrónico de restablecimiento de contraseña
                            mAuth.sendPasswordResetEmail(email)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(MainActivity.this, "Correo electrónico de restablecimiento de contraseña enviado.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this, "Error al enviar el correo electrónico de restablecimiento de contraseña.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(MainActivity.this, "Por favor, introduce tu correo electrónico.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancelar", null);

                // Crear y mostrar el AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
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

                            // Guardar el estado del CheckBox y las credenciales del usuario
                            if (checkBox.isChecked()) {
                                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                        .putBoolean("isRemembered", true)
                                        .putString("email", correo)
                                        .putString("password", contra)
                                        .apply();
                            }
                        }
                    } else {
                        // Si el inicio de sesión falla, mostrar un mensaje al usuario.
                        Toast.makeText(MainActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
