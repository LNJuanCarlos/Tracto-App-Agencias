package com.ilender.transportesforilender.model;

public class Seguimientoruta {

    private String idSeguimiento;
    private String ruta;
    private String fecha;
    private String estado;
    private String latitud;
    private String longitud;
    private String kilometraje;
    private String observacion;

    public Seguimientoruta(){

    }

    public Seguimientoruta(String ruta, String fecha, String estado, String latitud, String longitud, String kilometraje, String observacion) {
        this.ruta = ruta;
        this.fecha = fecha;
        this.estado = estado;
        this.latitud = latitud;
        this.longitud = longitud;
        this.kilometraje = kilometraje;
        this.observacion = observacion;
    }

    public Seguimientoruta(String ruta, String fecha, String estado, String latitud, String longitud, String kilometraje) {
        this.ruta = ruta;
        this.fecha = fecha;
        this.estado = estado;
        this.latitud = latitud;
        this.longitud = longitud;
        this.kilometraje = kilometraje;
    }

    public Seguimientoruta(String ruta, String fecha, String latitud, String longitud, String kilometraje) {
        this.ruta = ruta;
        this.fecha = fecha;
        this.latitud = latitud;
        this.longitud = longitud;
        this.kilometraje = kilometraje;
    }

    public String getIdSeguimiento() {
        return idSeguimiento;
    }

    public void setIdSeguimiento(String idSeguimiento) {
        this.idSeguimiento = idSeguimiento;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(String kilometraje) {
        this.kilometraje = kilometraje;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
