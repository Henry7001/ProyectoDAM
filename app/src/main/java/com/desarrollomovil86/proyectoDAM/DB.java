package com.desarrollomovil86.proyectoDAM;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class DB extends SQLiteOpenHelper {
    public static final String NombreDB = "Login.db";
    public static final String TablaUsuarios = "usuarios";
    public static final String ColUsername = "username";
    public static final String ColPassword = "password";
    public static final String TablaEstudiantes = "estudiantes";
    public static final String ColId = "id";
    public static final String ColCedula = "cedula";
    public static final String ColNombre = "nombre";
    public static final String ColApellido = "apellido";
    public static final String ColCorreo = "correo";
    public static final String ColCelular = "celular";
    public static final String ColDireccion = "direccion";
    public static final String ColCarrera = "carrera";
    public static final String ColSemestre = "semestre";
    public static final String ColFoto = "foto";
    public static final String ColSaludoAudio = "saludo_audio";
    public static final String ColTituloPDF = "titulo_pdf";
    public static final String ColEstado = "estado";

    private static final int VersionDB = 1;

    public DB(Context context) {
        super(context, NombreDB, null, VersionDB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TablaUsuarios + " (" +
                ColUsername + " TEXT PRIMARY KEY," +
                ColPassword + " TEXT" +
                ")";
        db.execSQL(createTableQuery);
        createTableQuery = "CREATE TABLE " + TablaEstudiantes + " (" +
                ColId + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ColCedula + " TEXT," +
                ColNombre + " TEXT," +
                ColApellido + " TEXT," +
                ColCorreo + " TEXT," +
                ColCelular + " TEXT," +
                ColDireccion + " TEXT," +
                ColCarrera + " TEXT," +
                ColSemestre + " TEXT," +
                ColFoto + " BLOB," +
                ColSaludoAudio + " BLOB," +
                ColTituloPDF + " BLOB," +
                ColEstado + " TEXT DEFAULT 'A'" +
                ")";

        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implementar si se requieren cambios en la estructura de la tabla en futuras actualizaciones.
    }

    public int agregarUsuario(String username, String password) {

        if(username.isEmpty() || password.isEmpty()){
            return 0;
        }

        if(this.usuarioExiste(username, password)){
            return 1;
        }else{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ColUsername, username);
            contentValues.put(ColPassword, password);

            long resultado = db.insert(TablaUsuarios, null, contentValues);
            if(resultado != -1){
                return 2;
            }else{
                return 3;
            }
        }
    }

    public boolean usuarioExiste(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columnas = {ColUsername};
        String seleccion = ColUsername + " = ? AND " + ColPassword + " = ?";
        String[] argumentos = {username, password};

        Cursor cursor = db.query(TablaUsuarios, columnas, seleccion, argumentos, null, null, null);
        int count = cursor.getCount();
        cursor.close();

        return count > 0;
    }


    public static List<Estudiante> obtenerEstudiantes(Context context) {
        List<Estudiante> listaEstudiantes = new ArrayList<>();
        SQLiteDatabase db = new DB(context).getReadableDatabase();
        String[] columnas = {
                ColId, ColCedula, ColNombre, ColApellido, ColCorreo, ColCelular,
                ColDireccion, ColCarrera, ColSemestre, ColFoto, ColSaludoAudio,
                ColTituloPDF, ColEstado
        };

        Cursor cursor = db.query(TablaEstudiantes, columnas, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Estudiante estudiante = new Estudiante();
                estudiante.setId(cursor.getInt(0));
                estudiante.setCedula(cursor.getString(1));
                estudiante.setNombre(cursor.getString(2));
                estudiante.setApellido(cursor.getString(3));
                estudiante.setCorreo(cursor.getString(4));
                estudiante.setCelular(cursor.getString(5));
                estudiante.setDireccion(cursor.getString(6));
                estudiante.setCarrera(cursor.getString(7));
                estudiante.setSemestre(cursor.getString(8));
                estudiante.setFoto(cursor.getBlob(9));
                estudiante.setSaludoAudio(cursor.getBlob(10));
                estudiante.setTituloPDF(cursor.getBlob(11));
                estudiante.setEstado(cursor.getString(12));
                listaEstudiantes.add(estudiante);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return listaEstudiantes;
    }

    public static List<Estudiante> buscarEstudiantes(Context context, String atributo, String valor) {
        List<Estudiante> listaEstudiantes = new ArrayList<>();
        SQLiteDatabase db = new DB(context).getReadableDatabase();
        String[] columnas = {
                ColId, ColCedula, ColNombre, ColApellido, ColCorreo, ColCelular,
                ColDireccion, ColCarrera, ColSemestre, ColFoto, ColSaludoAudio,
                ColTituloPDF, ColEstado
        };

        String whereClause = null;
        String[] whereArgs = null;

        if (atributo != null && !atributo.isEmpty() && valor != null && !valor.isEmpty()) {
            // Use the LIKE statement for pattern matching
            whereClause = atributo + " LIKE ?";
            // Modify the value to include the pattern (e.g., %value%)
            whereArgs = new String[]{"%" + valor + "%"};
        }

        Cursor cursor = db.query(TablaEstudiantes, columnas, whereClause, whereArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Estudiante estudiante = new Estudiante();
                estudiante.setId(cursor.getInt(0));
                estudiante.setCedula(cursor.getString(1));
                estudiante.setNombre(cursor.getString(2));
                estudiante.setApellido(cursor.getString(3));
                estudiante.setCorreo(cursor.getString(4));
                estudiante.setCelular(cursor.getString(5));
                estudiante.setDireccion(cursor.getString(6));
                estudiante.setCarrera(cursor.getString(7));
                estudiante.setSemestre(cursor.getString(8));
                estudiante.setFoto(cursor.getBlob(9));
                estudiante.setSaludoAudio(cursor.getBlob(10));
                estudiante.setTituloPDF(cursor.getBlob(11));
                estudiante.setEstado(cursor.getString(12));
                listaEstudiantes.add(estudiante);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return listaEstudiantes;
    }


    public long insertarEstudiante(Estudiante estudiante) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ColCedula, estudiante.getCedula());
        contentValues.put(ColNombre, estudiante.getNombre());
        contentValues.put(ColApellido, estudiante.getApellido());
        contentValues.put(ColCorreo, estudiante.getCorreo());
        contentValues.put(ColCelular, estudiante.getCelular());
        contentValues.put(ColDireccion, estudiante.getDireccion());
        contentValues.put(ColCarrera, estudiante.getCarrera());
        contentValues.put(ColSemestre, estudiante.getSemestre());
        contentValues.put(ColFoto, estudiante.getFoto());
        contentValues.put(ColSaludoAudio, estudiante.getSaludoAudio());
        contentValues.put(ColTituloPDF, estudiante.getTituloPDF());
        contentValues.put(ColEstado, estudiante.getEstado());

        long newRowId = db.insert(TablaEstudiantes, null, contentValues);
        db.close();
        return newRowId;
    }

}
