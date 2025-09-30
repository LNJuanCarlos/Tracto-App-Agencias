package com.ilender.transportesforilender.model;

public class Ruta {

    private String idRuta;
    private String chofer;
    private String vehiculo;
    private String estado;
    private String fecha;

    // Mantengo el campo direccion para compatibilidad
    private String direccion;

    // Nuevo campo para diferenciar el destino
    private String tipoEntrega; // "AGENCIA" o "DELIVERY"

    // Si es agencia
    private String agencia; // nombre o id de la agencia

    // Si es delivery
    private String cliente; // nombre o id del cliente

    public Ruta(){

    }


    // Constructor para Delivery
    public Ruta(String chofer, String vehiculo, String estado, String fecha, String cliente, String direccion) {
        this.chofer = chofer;
        this.vehiculo = vehiculo;
        this.estado = estado;
        this.fecha = fecha;
        this.tipoEntrega = "DELIVERY";
        this.cliente = cliente;
        this.direccion = direccion; // compatibilidad con tu campo actual
    }

    // Constructor para Agencia
    public Ruta(String chofer, String vehiculo, String estado, String fecha, String agencia) {
        this.chofer = chofer;
        this.vehiculo = vehiculo;
        this.estado = estado;
        this.fecha = fecha;
        this.tipoEntrega = "AGENCIA";
        this.agencia = agencia;
        this.direccion = agencia; // compatibilidad con tu campo actual
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

    public String getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

}
