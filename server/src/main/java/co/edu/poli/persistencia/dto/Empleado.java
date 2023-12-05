package co.edu.poli.persistencia.dto;

public class Empleado {
    private int id;
    private String identificacion;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String email;
    private String fechaNacimiento;
    private String salario;
    private String comision;
    private int cargoId;
    private int departamentoId;
    private int gerenteId;
    private boolean estado;

    public Empleado(String[] datos){
        this.id = getParseInt(datos[0].trim());
        this.identificacion = datos[1];
        this.primerNombre = datos[2];
        this.segundoNombre = datos[3];
        this.primerApellido = datos[4];
        this.segundoApellido = datos[5];
        this.email = datos[6];
        this.fechaNacimiento = datos[7];
        this.salario = datos[8];
        this.comision = datos[9];
        this.cargoId = getParseInt(datos[10].trim());
        this.departamentoId = getParseInt(datos[11].trim());
        this.gerenteId = getParseInt(datos[12].trim());
        this.estado = Boolean.parseBoolean(datos[13]);
    }

    private static int getParseInt(String datos) {
        if (datos.isEmpty() || datos.equals("null")) {
            return 0;
        }

        return Integer.parseInt(datos);
    }

    public int getId() {
        return id;
    }

    public void setId(String id) {
        this.id = getParseInt(id);
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getPrimerNombre() {
        if (primerNombre.isEmpty() || primerNombre.equals("null")) {
            return "";
        }

        return primerNombre;
    }

    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    public String getSegundoNombre() {
        if (segundoNombre.isEmpty() || segundoNombre.equals("null")) {
            return "";
        }

        return segundoNombre;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public String getPrimerApellido() {
        if (primerApellido.isEmpty() || primerApellido.equals("null")) {
            return "";
        }

        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        if (segundoApellido.isEmpty() || segundoApellido.equals("null")) {
            return "";
        }
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getEmail() {
        if (email.isEmpty() || email.equals("null")) {
            return "";
        }

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

    public String getComision() {
        if (comision.isEmpty() || comision.equals("null")) {
            return "";
        }
        return comision;
    }

    public void setComision(String comision) {
        this.comision = comision;
    }

    public int getCargoId() {
        return cargoId;
    }

    public void setCargoId(String cargoId) {
        this.cargoId = getParseInt(cargoId);
    }

    public int getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(String departamentoId) {
        this.departamentoId = getParseInt(departamentoId);
    }

    public int getGerenteId() {
        return gerenteId;
    }

    public void setGerenteId(String gerenteId) {
        this.gerenteId = getParseInt(gerenteId);
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getFullName() {
        return primerNombre + " " + primerApellido;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "id='" + id + '\'' +
                ", identificacion='" + identificacion + '\'' +
                ", primerNombre='" + primerNombre + '\'' +
                ", segundoNombre='" + segundoNombre + '\'' +
                ", primerApellido='" + primerApellido + '\'' +
                ", segundoApellido='" + segundoApellido + '\'' +
                ", email='" + email + '\'' +
                ", fechaNacimiento='" + fechaNacimiento + '\'' +
                ", salario='" + salario + '\'' +
                ", comision='" + comision + '\'' +
                ", cargoId='" + cargoId + '\'' +
                ", departamentoId='" + departamentoId + '\'' +
                ", gerenteId='" + gerenteId + '\'' +
                ", estado=" + estado +
                '}';
    }
}
