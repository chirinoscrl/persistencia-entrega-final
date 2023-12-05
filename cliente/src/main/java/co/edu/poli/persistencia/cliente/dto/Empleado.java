package co.edu.poli.persistencia.cliente.dto;

public class Empleado {
    private String id;
    private String identificacion;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String email;
    private String fechaNacimiento;
    private String salario;
    private String direccion;
    private String idCiudad;
    private String idTipoIdentificacion;
    private String idCargo;

    public Empleado(String[] datos){
        this.id = datos[0];
        this.identificacion = datos[1];
        this.primerNombre = datos[2];
        this.segundoNombre = datos[3];
        this.primerApellido = datos[4];
        this.segundoApellido = datos[5];
        this.email = datos[6];
        this.fechaNacimiento = datos[7];
        this.salario = datos[8];
        this.direccion = datos[9];
        this.idCiudad = datos[10];
        this.idTipoIdentificacion = datos[11];
        this.idCargo = datos[12];
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getPrimerNombre() {
        return primerNombre;
    }

    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    public String getSegundoNombre() {
        return segundoNombre;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSalario() {
        return salario;
    }

    public void setSalario(String salario) {
        this.salario = salario;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(String idCiudad) {
        this.idCiudad = idCiudad;
    }

    public String getIdTipoIdentificacion() {
        return idTipoIdentificacion;
    }

    public void setIdTipoIdentificacion(String idTipoIdentificacion) {
        this.idTipoIdentificacion = idTipoIdentificacion;
    }

    public String getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(String idCargo) {
        this.idCargo = idCargo;
    }

    public String getFullName() {
        return primerNombre + " " + primerApellido;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "identificacion='" + identificacion + '\'' +
                ", primerNombre='" + primerNombre + '\'' +
                ", primerApellido='" + primerApellido + '\'' +
                ", idTipoIdentificacion='" + idTipoIdentificacion + '\'' +
                '}';
    }
}
