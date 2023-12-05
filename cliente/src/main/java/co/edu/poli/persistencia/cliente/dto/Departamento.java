package co.edu.poli.persistencia.cliente.dto;

import java.util.List;
import java.util.Optional;

public class Departamento {
    private String id;
    private String nombre;
    private int localizacionId;

    public Departamento(String[] datos) {
        this.id = datos[0].trim();
        this.nombre = datos[1].trim();
        this.localizacionId = Integer.parseInt(datos[2].trim());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getLocalizacionId() {
        return localizacionId;
    }

    public void setLocalizacionId(int localizacionId) {
        this.localizacionId = localizacionId;
    }
}
