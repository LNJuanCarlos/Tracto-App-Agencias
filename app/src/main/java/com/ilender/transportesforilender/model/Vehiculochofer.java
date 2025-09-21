package com.ilender.transportesforilender.model;

public class Vehiculochofer {

    private String id;
    private String chofer;
    private String fecha;
    private String vehiculo;

    public Vehiculochofer(){

    }

    public Vehiculochofer(String chofer, String fecha, String vehiculo) {
        this.chofer = chofer;
        this.fecha = fecha;
        this.vehiculo = vehiculo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChofer() {
        return chofer;
    }

    public void setChofer(String chofer) {
        this.chofer = chofer;
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

    @Override
    public String toString() {
        // 👇 Aquí decides qué mostrar en el Spinner
        return  chofer;
    }
}
