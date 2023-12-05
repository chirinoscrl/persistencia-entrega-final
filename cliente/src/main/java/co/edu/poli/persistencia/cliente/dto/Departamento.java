package co.edu.poli.persistencia.cliente.dto;

public class Departamento {
    private int id;
    private String nombre;
    private int localizacionId;

    public Departamento(String[] datos) {
        this.id = Integer.parseInt(datos[0].trim());
        this.nombre = datos[1].trim();
        this.localizacionId = Integer.parseInt(datos[2].trim());
    }

    public int getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Integer.parseInt(id);
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
