package com.desarrollomovil86.proyectoDAM;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editTextUsername = findViewById(R.id.username), editTextPassword = findViewById(R.id.password);
        Button buttonLogin = findViewById(R.id.login), buttonSignIn = findViewById(R.id.signin);

        DB db = new DB(this);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                int usuarioAgregado = db.agregarUsuario(username, password);
                switch (usuarioAgregado) {
                    case 2:
                        mostrarMensaje("Información", "Usuario agregado");
                        break;
                    case 1:
                        mostrarMensaje("Error", "Ya existe ese usuario.");
                        break;
                    case 0:
                        mostrarMensaje("Error", "No se han ingresado datos en alguna de las entradas.");
                        break;
                    default:
                        mostrarMensaje("Error", "No se pudo añadir el usuario.");
                        break;
                }

            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                boolean usuarioExiste = db.usuarioExiste(username, password);

                if (usuarioExiste) {
                    showToast("Bienvenid@, " + username);
                    Intent intent = new Intent(MainActivity.this, registros.class);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);

                } else {
                    if(username.isEmpty() || password.isEmpty()){
                        mostrarMensaje("Error de inicio de sesión", "No se han ingresado datos en alguna de las entradas.");
                    }else{
                        mostrarMensaje("Error de inicio de sesión", "Datos invalidos");
                    }
                }
            }
        });
    }

    private void mostrarMensaje(String titulo, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setMessage(message);
        builder.setPositiveButton("Aceptar", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}