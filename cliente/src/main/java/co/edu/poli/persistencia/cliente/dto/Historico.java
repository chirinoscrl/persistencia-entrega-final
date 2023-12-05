package co.edu.poli.persistencia.cliente.dto;

import javafx.beans.property.SimpleStringProperty;

public class Historico {
    private SimpleStringProperty fechaRetiro;
    private SimpleStringProperty cargo;
    private SimpleStringProperty departamento;
    private SimpleStringProperty localizacion;
    private SimpleStringProperty ciudad;

    public Historico(String[] properties) {
        this.fechaRetiro = new SimpleStringProperty(properties[4]);
        this.cargo = new SimpleStringProperty(properties[15]);
        this.departamento = new SimpleStringProperty(properties[6]);
        this.localizacion = new SimpleStringProperty(properties[9]);
        this.ciudad = new SimpleStringProperty(properties[12]);
    }

    public String getFechaRetiro() {
        return fechaRetiro.get();
    }

    public SimpleStringProperty fechaRetiroProperty() {
        return fechaRetiro;
    }

    public void setFechaRetiro(String fechaRetiro) {
        this.fechaRetiro.set(fechaRetiro);
    }

    public String getCargo() {
        return cargo.get();
    }

    public SimpleStringProperty cargoProperty() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo.set(cargo);
    }

    public String getDepartamento() {
        return departamento.get();
    }

    public SimpleStringProperty departamentoProperty() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento.set(departamento);
    }

    public String getLocalizacion() {
        return localizacion.get();
    }

    public SimpleStringProperty localizacionProperty() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion.set(localizacion);
    }

    public String getCiudad() {
        return ciudad.get();
    }

    public SimpleStringProperty ciudadProperty() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad.set(ciudad);
    }
}
