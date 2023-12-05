package co.edu.poli.persistencia.cliente.dto;

public class Cargo {
    private String id;
    private String nombre;
    private int salarioBasico;
    private int salarioTotal;

    public Cargo(String[] datos){
        this.id = datos[0].trim();
        this.nombre = datos[1].trim();
        this.salarioBasico = Integer.parseInt(datos[2].trim());
        this.salarioTotal = Integer.parseInt(datos[3].trim());
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

    public int getSalarioBasico() {
        return salarioBasico;
    }

    public void setSalarioBasico(int salarioBasico) {
        this.salarioBasico = salarioBasico;
    }

    public int getSalarioTotal() {
        return salarioTotal;
    }

    public void setSalarioTotal(int salarioTotal) {
        this.salarioTotal = salarioTotal;
    }
}