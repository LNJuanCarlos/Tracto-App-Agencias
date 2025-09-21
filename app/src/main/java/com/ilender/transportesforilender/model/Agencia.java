package com.ilender.transportesforilender.model;

public class Agencia {

    private String idAgencia;
    private String nombre;
    private String direccion;
    private String telefono;

    public Agencia() {
    }

    public Agencia(String idAgencia, String nombre, String direccion, String telefono) {
        this.idAgencia = idAgencia;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public String getIdAgencia() {
        return idAgencia;
    }

    public void setIdAgencia(String idAgencia) {
        this.idAgencia = idAgencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return nombre; // 🔥 Esto hará que en el Spinner aparezca el nombre
    }
}