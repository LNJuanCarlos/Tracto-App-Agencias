package com.ilender.transportesforilender.model;

public class Usuarios {

    private String correo;
    private String tipo;
    private String nombres;
    private String chofer;

    public Usuarios(){

    }

    public Usuarios(String correo, String tipo, String nombres) {
        this.correo = correo;
        this.tipo = tipo;
        this.nombres = nombres;
    }

    public Usuarios(String correo, String tipo, String nombres, String chofer) {
        this.correo = correo;
        this.tipo = tipo;
        this.nombres = nombres;
        this.chofer = chofer;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getChofer() {
        return chofer;
    }

    public void setChofer(String chofer) {
        this.chofer = chofer;
    }
}
