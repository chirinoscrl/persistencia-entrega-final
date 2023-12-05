package co.edu.poli.persistencia.cliente.dto;

public class Cargo {
    private int id;
    private String nombre;
    private String salarioBasico;
    private String salarioTotal;

    public Cargo(String[] datos){
        this.id = Integer.parseInt(datos[0].trim());
        this.nombre = datos[1].trim();
        this.salarioBasico = datos[2].trim();
        this.salarioTotal = datos[3].trim();
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

    public String getSalarioBasico() {
        return salarioBasico;
    }

    public void setSalarioBasico(String salarioBasico) {
        this.salarioBasico = salarioBasico;
    }

    public String getSalarioTotal() {
        return salarioTotal;
    }

    public void setSalarioTotal(String salarioTotal) {
        this.salarioTotal = salarioTotal;
    }
}
