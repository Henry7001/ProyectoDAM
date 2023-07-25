package com.desarrollomovil86.proyectoDAM;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaPlayer;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Estudiante {
    private int id;
    private String cedula;
    private String nombre;
    private String apellido;
    private String correo;
    private String celular;
    private String direccion;
    private String carrera;
    private String semestre;
    private byte[] foto;
    private byte[] saludoAudio;
    private byte[] tituloPDF;
    private String estado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public byte[] getSaludoAudio() {
        return saludoAudio;
    }

    public void setSaludoAudio(byte[] saludoAudio) {
        this.saludoAudio = saludoAudio;
    }

    public byte[] getTituloPDF() {
        return tituloPDF;
    }

    public void setTituloPDF(byte[] tituloPDF) {
        this.tituloPDF = tituloPDF;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}