package com.ilender.transportesforilender.model;

public class Vehiculos {

    private String idVehiculos;
    private String marca;
    private String placa;

    public Vehiculos(){

    }

    public Vehiculos(String marca, String placa) {
        this.marca = marca;
        this.placa = placa;
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
