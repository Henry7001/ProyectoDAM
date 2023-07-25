package com.desarrollomovil86.proyectoDAM;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class registros extends AppCompatActivity {

    private List<Estudiante> estudiantesList;
    private RecyclerView recyclerViewEstudiantes;
    private EstudiantesAdapter estudiantesAdapter;
    private Spinner spinnerOptions;
    private EditText txtBusqueda;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros);

        // Initialize the RecyclerView and its adapter
        recyclerViewEstudiantes = findViewById(R.id.recyclerViewEstudiantes);
        estudiantesList = DB.obtenerEstudiantes(this);
        estudiantesAdapter = new EstudiantesAdapter(estudiantesList, this);

        // Set the RecyclerView adapter
        recyclerViewEstudiantes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEstudiantes.setAdapter(estudiantesAdapter);

        // Obtener referencia al Spinner y al EditText desde el layout
        spinnerOptions = findViewById(R.id.spinner_options);
        txtBusqueda = findViewById(R.id.txtBusqueda);

        // Definir las opciones para el Spinner
        String[] opciones = {"Nombre", "Apellido", "Cedula", "Id", "Carrera", "Semestre"};

        // Crear un ArrayAdapter usando el array de opciones y un estilo predeterminado para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);

        // Especificar el diseño que se usará cuando el Spinner se despliegue
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Configurar el ArrayAdapter en el Spinner
        spinnerOptions.setAdapter(adapter);

        // Agregar listener para detectar cambios en el Spinner
        spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String atributoSeleccionado = spinnerOptions.getSelectedItem().toString();
                String valorBusqueda = txtBusqueda.getText().toString();
                estudiantesAdapter.filtrarEstudiantes(atributoSeleccionado, valorBusqueda);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button botonRegistrar = findViewById(R.id.botonRegistrar);
        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirActivity(EstudianteForm.class); // Reemplaza NuevoActivity por el nombre del Activity al que deseas ir
            }
        });

        txtBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String atributoSeleccionado = spinnerOptions.getSelectedItem().toString();
                String valorBusqueda = txtBusqueda.getText().toString();
                estudiantesAdapter.filtrarEstudiantes(atributoSeleccionado, valorBusqueda);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // You can leave this method empty, as it's not needed for the filtering
            }
        });



        TextView nombreUsuario = findViewById(R.id.nombreUsuario);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("USERNAME")) {
            String username = intent.getStringExtra("USERNAME");

            nombreUsuario.setText("Usuario: " + username);
        } else {
            nombreUsuario.setText("Usuario no encontrado");
        }


    }

    private void abrirActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

}