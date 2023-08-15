package com.desarrollomovil86.proyectoDAM;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EstudianteEdit extends AppCompatActivity {
    private static final int ESTUDIANTE_EDIT_REQUEST_CODE = 200;
    DB db = new DB(this);
    Estudiante nuevo = new Estudiante();
    private TextView lblID;
    private EditText editTextCedula;
    private EditText editTextNombre;
    private EditText editTextApellido;
    private EditText editTextCorreo;
    private EditText editTextCelular;
    private EditText editTextDireccion;
    private EditText editTextCarrera;
    private EditText editTextSemestre;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 200;
    private static final int REQUEST_PICK_PDF = 201;
    private static final int REQUEST_PICK_IMAGE = 202;
    private static final int MAX_PDF_SIZE_BYTES = 2 * 1024 * 1024;
    private boolean isRecording = false;
    private String audioFilePath;
    private MediaRecorder mediaRecorder;

    private TextView textViewAudioStatus;
    private TextView textViewPDFStatus;
    private TextView textViewImagenStatus;

    private Estudiante viejo;
    private int idviejo;
    private EstudiantesAdapter estudiantesAdapter;

    public EstudianteEdit() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estudiante_edit);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("id")) {
            idviejo = intent.getIntExtra("id",0);
        }

        viejo = DB.obtenerEstudiantePorId(EstudianteEdit.this,idviejo);

        lblID = findViewById(R.id.nombreUsuario);
        editTextCedula = findViewById(R.id.editTextCedula);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextApellido = findViewById(R.id.editTextApellido);
        editTextCorreo = findViewById(R.id.editTextCorreo);
        editTextCelular = findViewById(R.id.editTextCelular);
        editTextDireccion = findViewById(R.id.editTextDireccion);
        editTextCarrera = findViewById(R.id.editTextCarrera);
        editTextSemestre = findViewById(R.id.editTextSemestre);
        textViewAudioStatus = findViewById(R.id.textViewAudioStatus);
        textViewPDFStatus = findViewById(R.id.textViewPDFStatus);
        textViewImagenStatus = findViewById(R.id.textViewImagenStatus);

        Button btnGrabarAudio = findViewById(R.id.btnGrabarAudio);
        Button btnSeleccionarPDF = findViewById(R.id.btnSeleccionarPDF);
        Button btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);

        Button btnInsertar = findViewById(R.id.btnInsertar);

        lblID.setText("Modificando al estudiante con id: "+viejo.getId());
        editTextCedula.setText(viejo.getCedula() != null ? viejo.getCedula() : "");
        editTextNombre.setText(viejo.getNombre() != null ? viejo.getNombre() : "");
        editTextApellido.setText(viejo.getApellido() != null ? viejo.getApellido() : "");
        editTextCorreo.setText(viejo.getCorreo() != null ? viejo.getCorreo() : "");
        editTextCelular.setText(viejo.getCelular() != null ? viejo.getCelular() : "");
        editTextDireccion.setText(viejo.getDireccion() != null ? viejo.getDireccion() : "");
        editTextCarrera.setText(viejo.getCarrera() != null ? viejo.getCarrera() : "");
        editTextSemestre.setText(viejo.getSemestre() != null ? viejo.getSemestre() : "");

        btnGrabarAudio.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording(this);
            }
        });

        btnSeleccionarPDF.setOnClickListener(v -> pickPDFFromGallery());

        btnSeleccionarImagen.setOnClickListener(v -> pickImageFromGallery());

        btnInsertar.setOnClickListener(v->EditData(viejo));
    }
    private void startRecording(Context context) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Audio_" + timeStamp + ".m4a";

        File audioFile = new File(context.getCacheDir(), fileName);
        audioFilePath = audioFile.getAbsolutePath();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            textViewAudioStatus.setText("Grabando...");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al iniciar la grabación de audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            textViewAudioStatus.setText("Audio grabado: " + getFileNameFromUri(audioFilePath));

            try {
                File file = new File(audioFilePath);
                byte[] bytes = new byte[(int) file.length()];
                try (FileInputStream fis = new FileInputStream(file)) {
                    fis.read(bytes);
                }
                nuevo.setSaludoAudio(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void pickPDFFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, REQUEST_PICK_PDF);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_PDF && data != null) {
                Uri selectedPDFUri = data.getData();
                long fileSize = getFileSize(selectedPDFUri);
                if (fileSize <= MAX_PDF_SIZE_BYTES) {
                    String pdfFileName = getFileNameFromUri(selectedPDFUri);
                    textViewPDFStatus.setText("PDF seleccionado: " + pdfFileName);
                    new ReadPDFDataTask().execute(selectedPDFUri);
                } else {
                    Toast.makeText(this, "El archivo PDF seleccionado es demasiado grande (máximo 2 MB).", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                Uri selectedImageUri = data.getData();
                String imageFileName = getFileNameFromUri(selectedImageUri);
                textViewImagenStatus.setText("Imagen seleccionada: " + imageFileName);
                new ReadImageDataTask().execute(selectedImageUri);
            }
        }
    }

    private long getFileSize(Uri uri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.MediaColumns.SIZE};
            cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE);
                return cursor.getLong(sizeIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording(this);
            } else {
                Toast.makeText(this, "Permiso de grabación de audio denegado.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording(this);
            } else {
                Toast.makeText(this, "Permiso de almacenamiento denegado.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String displayName = null;
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = null;

        try {
            cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    displayName = cursor.getString(nameIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return displayName;
    }

    private String getFileNameFromUri(String uriString) {
        String path = uriString.substring(7);
        File file = new File(path);
        return file.getName();
    }

    private class ReadPDFDataTask extends AsyncTask<Uri, Void, byte[]> {
        @Override
        protected byte[] doInBackground(Uri... uris) {
            Uri pdfUri = uris[0];
            byte[] pdfData = null;
            try {
                ContentResolver contentResolver = getContentResolver();
                pdfData = readBytesFromUri(contentResolver, pdfUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return pdfData;
        }

        @Override
        protected void onPostExecute(byte[] pdfData) {
            if (pdfData != null) {
                nuevo.setTituloPDF(null);
                nuevo.setTituloPDF(pdfData);
            } else {

            }
        }
    }

    private class ReadImageDataTask extends AsyncTask<Uri, Void, byte[]> {
        @Override
        protected byte[] doInBackground(Uri... uris) {
            Uri imageUri = uris[0];
            byte[] imageData = null;
            try {
                ContentResolver contentResolver = getContentResolver();
                imageData = readBytesFromUri(contentResolver, imageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return imageData;
        }

        @Override
        protected void onPostExecute(byte[] imageData) {
            if (imageData != null) {
                nuevo.setFoto(imageData);
            } else {
            }
        }
    }

    private byte[] readBytesFromUri(ContentResolver contentResolver, Uri uri) throws IOException {
        InputStream inputStream = contentResolver.openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        if (inputStream != null) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            inputStream.close();
        }
        return byteBuffer.toByteArray();
    }

    public void EditData(Estudiante viejo){
        nuevo.setId(viejo.getId());
        nuevo.setCedula(editTextCedula.getText().toString() == null ? viejo.getCedula() : editTextCedula.getText().toString());
        nuevo.setNombre(editTextNombre.getText().toString() == null ? viejo.getNombre() : editTextNombre.getText().toString());
        nuevo.setApellido(editTextApellido.getText().toString() == null ? viejo.getApellido() : editTextApellido.getText().toString());
        nuevo.setCorreo(editTextCorreo.getText().toString() == null ? viejo.getCorreo() : editTextCorreo.getText().toString());
        nuevo.setCelular(editTextCelular.getText().toString() == null ? viejo.getCelular() : editTextCelular.getText().toString());
        nuevo.setDireccion(editTextDireccion.getText().toString() == null ? viejo.getDireccion() : editTextDireccion.getText().toString());
        nuevo.setCarrera(editTextCarrera.getText().toString() == null ? viejo.getCarrera() : editTextCarrera.getText().toString());
        nuevo.setSemestre(editTextSemestre.getText().toString() == null ? viejo.getSemestre() : editTextSemestre.getText().toString());

        db.modificarEstudiante(this, nuevo);

        Toast.makeText(this, "Se ha modificado al estudiante con ID "+nuevo.getId(),Toast.LENGTH_SHORT).show();
        List<Estudiante> estudiantesList = DB.obtenerEstudiantes(EstudianteEdit.this);
        estudiantesAdapter.refreshEstudiantes(estudiantesList);
        finish();

    }

}