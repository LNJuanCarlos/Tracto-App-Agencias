package com.ilender.transportesforilender.model;

public class Clientes {

    private String idCliente;
    private String contacto;
    private String estado;
    private String identificador;
    private String nombres;
    private String telefono;

    public Clientes(){

    }

    public Clientes(String contacto, String estado, String identificador, String nombres, String telefono) {

        this.contacto = contacto;
        this.estado = estado;
        this.identificador = identificador;
        this.nombres = nombres;
        this.telefono = telefono;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String toString(){
        return nombres;
    }
}
