package com.ilender.transportesforilender.model;

public class Choferes {

    private String licencia;
    private String nombres;
    private String estado;
    private String telefono;
    private String idChofer;
    private String tipo;
    private String transportista;

    public Choferes(){

    }

    public Choferes(String licencia, String nombres, String estado, String telefono) {
        this.licencia = licencia;
        this.nombres = nombres;
        this.estado = estado;
        this.telefono = telefono;

    }

    public Choferes(String licencia, String nombres, String estado, String telefono, String tipo) {
        this.licencia = licencia;
        this.nombres = nombres;
        this.estado = estado;
        this.telefono = telefono;
        this.tipo = tipo;
    }

    public Choferes(String licencia, String nombres, String estado, String telefono, String tipo, String transportista) {
        this.licencia = licencia;
        this.nombres = nombres;
        this.estado = estado;
        this.telefono = telefono;
        this.tipo = tipo;
        this.transportista = transportista;
    }

    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getIdChofer() {
        return idChofer;
    }

    public void setIdChofer(String idChofer) {
        this.idChofer = idChofer;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTransportista() {
        return transportista;
    }

    public void setTransportista(String transportista) {
        this.transportista = transportista;
    }

    public String toString(){
        return nombres;
    }
}
