package com.ilender.transportesforilender.model;

public class Direccion {

    private String idDireccion;

    private String cliente;

    private String descripcion;

    private String distrito;

    private Direccion(){

    }

    public Direccion(String cliente, String descripcion, String distrito) {
        this.cliente = cliente;
        this.descripcion = descripcion;
        this.distrito = distrito;
    }

    public String getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(String idDireccion) {
        this.idDireccion = idDireccion;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String toString(){
        return distrito + " - " + descripcion;
    }
}
