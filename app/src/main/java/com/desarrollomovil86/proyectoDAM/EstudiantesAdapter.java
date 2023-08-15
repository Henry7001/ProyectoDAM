package com.desarrollomovil86.proyectoDAM;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class EstudiantesAdapter extends RecyclerView.Adapter<EstudiantesAdapter.EstudianteViewHolder> {
    private final Context context;
    private static final int REQUEST_EDIT_ESTUDIANTE = 1;
    private List<Estudiante> listaEstudiantes;
    private MediaPlayer mediaPlayer;

    public EstudiantesAdapter(List<Estudiante> listaEstudiantes, Context context) {
        this.listaEstudiantes = listaEstudiantes;
        this.context = context;
    }

    @NonNull
    @Override
    public EstudianteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_estudiante, parent, false);
        return new EstudianteViewHolder(view);
    }

    public Bitmap getFotoBitmap(byte[] foto) {
        if (foto != null) {
            ByteArrayInputStream imageStream = new ByteArrayInputStream(foto);
            return BitmapFactory.decodeStream(imageStream);
        }
        return null;
    }

    public void filtrarEstudiantes(String atributo, String valor) {
        listaEstudiantes = obtenerEstudiantesFiltrados(context, atributo, valor);
        notifyDataSetChanged();
    }

    public void Eliminar(int id){
        DB.eliminarEstudiante(context,id);
    }

    private List<Estudiante> obtenerEstudiantesFiltrados(Context context, String atributo, String valor) {
        return DB.buscarEstudiantes(context, atributo, valor);
    }

    @Override
    public void onBindViewHolder(@NonNull EstudianteViewHolder holder, int position) {
        Estudiante estudiante = listaEstudiantes.get(position);
        holder.txtID.setText(Html.fromHtml("<b>ID:</b> " + estudiante.getId()));
        holder.txtNombre.setText(Html.fromHtml("<b>Nombre:</b> " + estudiante.getNombre() + " " + estudiante.getApellido()));
        holder.txtCorreo.setText(Html.fromHtml("<b>Correo:</b> " + estudiante.getCorreo()));
        holder.txtCedula.setText(Html.fromHtml("<b>Cedula:</b> " + estudiante.getCedula()));
        holder.txtCelular.setText(Html.fromHtml("<b>Celular:</b> " + estudiante.getCelular()));
        holder.txtDireccion.setText(Html.fromHtml("<b>Dirección:</b> " + estudiante.getDireccion()));
        holder.txtCarrera.setText(Html.fromHtml("<b>Carrera:</b> " + estudiante.getCarrera()));
        holder.txtSemestre.setText(Html.fromHtml("<b>Semestre:</b> " + estudiante.getSemestre()));
        holder.imgFoto.setImageBitmap(this.getFotoBitmap(estudiante.getFoto()));
        holder.btnPlay.setOnClickListener(view -> reproducirSaludo(estudiante, context));
        holder.btnPDF.setOnClickListener(view -> {
            byte[] pdfData = estudiante.getTituloPDF();
            String pdfFileName = "pdf_" + estudiante.getCedula() + ".pdf";
            abrirPDF(pdfData, context, pdfFileName);
        });
        holder.btnEliminar.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Eliminar estudiante");
            builder.setMessage("¿Deseas eliminar este estudiante?");
            builder.setPositiveButton("Sí", (dialog, which) -> {
                this.Eliminar(estudiante.getId());
                Toast.makeText(context, "Estudiante eliminado", Toast.LENGTH_SHORT).show();
                List<Estudiante> nuevaLista = DB.obtenerEstudiantes(context);
                this.refreshEstudiantes(nuevaLista);
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            });
            builder.show();

        });
        holder.btnEditar.setOnClickListener(view -> {
            Intent intent = new Intent(this.context, EstudianteEdit.class);
            intent.putExtra("id", estudiante.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaEstudiantes.size();
    }

    static class EstudianteViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        TextView txtID;
        TextView txtCorreo;
        TextView txtCedula;
        TextView txtCelular;
        TextView txtCarrera;
        TextView txtDireccion;
        TextView txtSemestre;
        ImageView imgFoto;
        Button btnPlay;
        Button btnPDF;
        Button btnEliminar;
        Button btnEditar;
        public EstudianteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.Nombre);
            txtID = itemView.findViewById(R.id.id);
            txtCorreo = itemView.findViewById(R.id.Correo);
            txtCedula = itemView.findViewById(R.id.Cedula);
            txtCelular = itemView.findViewById(R.id.Celular);
            txtCarrera = itemView.findViewById(R.id.Carrera);
            txtDireccion = itemView.findViewById(R.id.Direccion);
            txtSemestre = itemView.findViewById(R.id.Semestre);
            imgFoto=itemView.findViewById(R.id.Foto);
            btnPlay= itemView.findViewById(R.id.Saludo);
            btnPDF = itemView.findViewById(R.id.VerPDF);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }

    private void reproducirSaludo(Estudiante estudiante, Context context) {
        File tempFile=null;
        try {
            byte[] mp3 = estudiante.getSaludoAudio();
            String tempFileName = "temp_saludo"+estudiante.getCedula()+".mp3";
            if (mp3 == null) {
                Log.println(Log.WARN, "Error",tempFileName);
                return;
            }
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            tempFile = new File(context.getCacheDir(), tempFileName);

            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(mp3);
            fos.close();

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(tempFile.getPath());

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mediaPlayer = null;
                }
            });

            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            tempFile.delete();
            Log.println(Log.WARN,"Borrado","Borrado");
        }
    }

    public void refreshEstudiantes(List<Estudiante> nuevosEstudiantes) {
        listaEstudiantes = nuevosEstudiantes;
        notifyDataSetChanged();
    }

    private void abrirPDF(byte[] pdfData, Context context, String pdfFileName) {
        try {
            File pdfFile = new File(context.getCacheDir(), pdfFileName);
            FileOutputStream fos = new FileOutputStream(pdfFile);
            fos.write(pdfData);
            fos.close();

            Uri pdfUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", pdfFile);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
