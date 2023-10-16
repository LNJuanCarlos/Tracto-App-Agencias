package com.ilender.transportesforilender.model;

public class Vehiculos {

    private String idVehiculos;
    private String marca;
    private String placa;

    private String tipo;

    private String transportista;

    public Vehiculos(){

    }

    public Vehiculos(String marca, String placa) {
        this.marca = marca;
        this.placa = placa;
    }

    public Vehiculos(String marca, String placa, String tipo) {
        this.marca = marca;
        this.placa = placa;
        this.tipo = tipo;
    }

    public Vehiculos(String marca, String placa, String tipo, String transportista) {
        this.marca = marca;
        this.placa = placa;
        this.tipo = tipo;
        this.transportista = transportista;
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

    public String getIdVehiculos() {
        return idVehiculos;
    }

    public void setIdVehiculos(String idVehiculos) {
        this.idVehiculos = idVehiculos;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String toString(){
        return marca + " - " + placa;
    }
}
