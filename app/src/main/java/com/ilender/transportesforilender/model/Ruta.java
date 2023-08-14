package com.ilender.transportesforilender.model;

public class Ruta {

    private String idRuta;
    private String chofer;
    private String direccion;
    private String estado;
    private String fecha;
    private String vehiculo;

    public Ruta(){

    }

    public Ruta(String chofer, String direccion, String estado, String fecha, String vehiculo) {
        this.chofer = chofer;
        this.direccion = direccion;
        this.estado = estado;
        this.fecha = fecha;
        this.vehiculo = vehiculo;
    }

    public String getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(String idRuta) {
        this.idRuta = idRuta;
    }

    public String getChofer() {
        return chofer;
    }

    public void setChofer(String chofer) {
        this.chofer = chofer;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
    }
}
