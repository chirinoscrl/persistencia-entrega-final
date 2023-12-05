package co.edu.poli.persistencia.cliente;

import co.edu.poli.persistencia.cliente.dto.Cargo;
import co.edu.poli.persistencia.cliente.dto.Departamento;
import co.edu.poli.persistencia.cliente.dto.Empleado;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ViewController {

    private static final Logger logger = LogManager.getLogger(ViewController.class);

    @FXML
    private TextField nicknameInput;

    @FXML
    private TextField docIdentidadEmpleado;

    @FXML
    private TextField primerNombreEmpleado;

    @FXML
    private TextField segundoNombreEmpleado;

    @FXML
    private TextField primerApellidoEmpleado;

    @FXML
    private TextField segundoApellidoEmpleado;

    @FXML
    private DatePicker fechaNacimientoEmpleado;

    @FXML
    private CheckBox estadoEmpleado;

    @FXML
    private TextField emailEmpleado;

    @FXML
    private TextField sueldoEmpleado;

    @FXML
    private TextField comision;

    String nickname;
    ClientConnect client;

    @FXML
    private TextField documentoEmpleadoBuscarField;

    @FXML
    private ComboBox<String> cargoEmpleado;
    ObservableList<String> cargos;

    @FXML
    private ComboBox<String> departamentoEmpleado;
    ObservableList<String> departamentos;

    @FXML
    private ComboBox<String> gerenteEmpleado;
    ObservableList<String> gerentes;
    private List<Departamento> listadoDepartamentos;
    private List<Cargo> listadoCargos;
    private List<Empleado> listadoGerentes;

    private List<Empleado> resultadoBusquedaEmpleado;

    /**
     * Este método normalmente es llamado después de que el archivo FXML ha sido cargado y el controlador ha sido creado.
     */
    @FXML
    protected void initialize() {
        cargos = FXCollections.observableArrayList();
        cargoEmpleado.setItems(cargos);

        departamentos = FXCollections.observableArrayList();
        departamentoEmpleado.setItems(departamentos);

        gerentes = FXCollections.observableArrayList();
        gerenteEmpleado.setItems(gerentes);
    }

    public void onConexion(ActionEvent actionEvent) {
        try {
            nickname = nicknameInput.getText();
            client = new ClientConnect(nickname,
                    cargos -> Platform.runLater(() -> obtenerListadoCargos(cargos)),
                    departamentos -> Platform.runLater(() -> obtenerListadoDepartamentos(departamentos)),
                    gerentes -> Platform.runLater(() -> obtenerListadoGerentes(gerentes)),
                    empleado -> Platform.runLater(() -> obtenerBusquedaEmpleado(empleado)),
                    actualizadoEmpleado -> Platform.runLater(() -> actualizadoEmpleado(actualizadoEmpleado))
            );

            // Deshabilita el botón de conexión y cambia la etiqueta de estado después de la conexión
            // connectButton.setDisable(true);
            //statusLabel.setText("Usuario conectado");
            //chatArea.appendText(String.format("Te acabas de conectar...\n"));

            new Thread(() -> {
                try {
                    while (true) {
                        client.readMessages();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            // Revisa el error, habilita el botón de conexión y muestra el error en la etiqueta de estado si la conexión falla
            // connectButton.setDisable(false);
            // statusLabel.setText("Falló la conexión: " + e.getMessage());
            logger.error(e);
        }
    }

    private void actualizadoEmpleado(String actualizadoEmpleado) {
        Platform.runLater(() -> {
            System.out.println(actualizadoEmpleado);
        });
    }

    private void obtenerBusquedaEmpleado(String empleadoStr) {
        Platform.runLater(() -> {
            System.out.println(empleadoStr);
            resultadoBusquedaEmpleado = new ArrayList<>();
            String[] empleadoStrings = empleadoStr.substring(1, empleadoStr.length() - 1).split(",");
            if (!empleadoStrings[0].equals("") && !empleadoStr.equals("null")) {
                for (String empleadoString : empleadoStrings) {
                    String[] properties = empleadoString.split("\\|");
                    Empleado empleado = new Empleado(properties);
                    resultadoBusquedaEmpleado.add(empleado);
                }
            }
            Empleado empleadoEncontrado = resultadoBusquedaEmpleado.get(0);
            System.out.println("Empleado encontrado: " + empleadoEncontrado);
            estadoEmpleado.setSelected(empleadoEncontrado.isEstado());
            docIdentidadEmpleado.setText(empleadoEncontrado.getIdentificacion());
            primerNombreEmpleado.setText(empleadoEncontrado.getPrimerNombre());
            segundoNombreEmpleado.setText(empleadoEncontrado.getSegundoNombre());
            primerApellidoEmpleado.setText(empleadoEncontrado.getPrimerApellido());
            segundoApellidoEmpleado.setText(empleadoEncontrado.getSegundoApellido());
            emailEmpleado.setText(empleadoEncontrado.getEmail());
            fechaNacimientoEmpleado.setValue(LocalDate.parse(empleadoEncontrado.getFechaNacimiento()));
            sueldoEmpleado.setText(empleadoEncontrado.getSalario());
            comision.setText(empleadoEncontrado.getComision());
            encontrarYSeleccionarCargo(empleadoEncontrado.getCargoId());
            encontrarYSeleccionarDepartamento(empleadoEncontrado.getDepartamentoId());
        });
    }

    private void encontrarYSeleccionarCargo(int cargoId) {
        for (Cargo cargo : listadoCargos) {
            if (cargo.getId() == cargoId) {
                cargoEmpleado.getSelectionModel().select(cargo.getNombre());
                break;
            }
        }
    }

    private void encontrarYSeleccionarDepartamento(int departamentoId) {
        for (Departamento departamento : listadoDepartamentos) {
            if (departamento.getId() == departamentoId) {
                departamentoEmpleado.getSelectionModel().select(departamento.getNombre());
                break;
            }
        }
    }

    public void onBuscarEmpleados(ActionEvent actionEvent) {
        String documentoEmpleadoBuscar = documentoEmpleadoBuscarField.getText();
        String sqlCommand = "@ClientNickname:buscarEmpleado:documentoIdentidad=" + documentoEmpleadoBuscar;

        try {
            client.sendMessage(sqlCommand);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void obtenerListadoCargos(String cargosStr) {
        Platform.runLater(() -> {
            System.out.println(cargosStr);
            listadoCargos = new ArrayList<>();
            cargos.clear();
            String[] cargoStrings = cargosStr.substring(1, cargosStr.length() - 1).split(",");

            if (!cargoStrings[0].equals("") && !cargosStr.equals("null")) {
                for (String cargoString : cargoStrings) {
                    String[] properties = cargoString.split("\\|");
                    Cargo cargo = new Cargo(properties);
                    listadoCargos.add(cargo);
                    cargos.add(cargo.getNombre());
                }
            }
        });
    }

    public void obtenerListadoDepartamentos(String departamentosStr) {
        Platform.runLater(() -> {
            System.out.println(departamentosStr);
            listadoDepartamentos = new ArrayList<>();
            departamentos.clear();
            String[] departamentosStrings = departamentosStr.substring(1, departamentosStr.length() - 1).split(",");
            if (!departamentosStrings[0].equals("") && !departamentosStr.equals("null")) {
                for (String departamentoString : departamentosStrings) {
                    String[] properties = departamentoString.split("\\|");
                    Departamento departamento = new Departamento(properties);
                    listadoDepartamentos.add(departamento);
                    departamentos.add(departamento.getNombre());
                }
            }
        });
    }

    public void handleChangeDepartamento(ActionEvent actionEvent) {
        String selectedItems = departamentoEmpleado.getSelectionModel().getSelectedItem();
        System.out.println("Selected item: " + selectedItems);
        Departamento departamentoId = buscarDepartamentoPorNombre(selectedItems).get();
        String msg = "@ClientNickname:obtenerListadoGerentes:" + departamentoId.getId();

        try {
            client.sendMessage(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void obtenerListadoGerentes(String gerentesStr) {
        Platform.runLater(() -> {
            System.out.println(gerentesStr);
            listadoGerentes = new ArrayList<>();
            gerentes.clear();
            String[] gerentesStrings = gerentesStr.substring(1, gerentesStr.length() - 1).split(",");

            if (!gerentesStrings[0].equals("") && !gerentesStr.equals("null")) {
                for (String gerenteString : gerentesStrings) {
                    String[] properties = gerenteString.split("\\|");
                    Empleado gerente = new Empleado(properties);
                    listadoGerentes.add(gerente);
                    gerentes.add(gerente.getFullName());
                }

                if (!Objects.equals(resultadoBusquedaEmpleado.get(0).getGerenteId(), "")) {
                    for (Empleado gerente : listadoGerentes) {
                        if (gerente.getId() == resultadoBusquedaEmpleado.get(0).getGerenteId()) {
                            gerenteEmpleado.getSelectionModel().select(gerente.getFullName());
                            break;
                        }
                    }
                }
            }
        });
    }

    public void crearEmpleado(ActionEvent actionEvent) {
        String docIdentidad = docIdentidadEmpleado.getText();
        String primerNombre = primerNombreEmpleado.getText();
        String segundoNombre = segundoNombreEmpleado.getText();
        String primerApellido = primerApellidoEmpleado.getText();
        String segundoApellido = segundoApellidoEmpleado.getText();
        LocalDate fechaNacimiento = fechaNacimientoEmpleado.getValue();
        boolean estado = estadoEmpleado.isSelected();
        Cargo cargo = buscarCargoPorNombre(cargoEmpleado.getSelectionModel().getSelectedItem()).get();
        String email = emailEmpleado.getText();
        String sueldo = sueldoEmpleado.getText();
        String comisionEmpleado = comision.getText();
        Departamento departamento = buscarDepartamentoPorNombre(departamentoEmpleado.getSelectionModel().getSelectedItem()).get();
        Empleado gerente = buscarGerentePorNombre(gerenteEmpleado.getSelectionModel().getSelectedItem()).get();

        StringBuilder empleadoInfo = new StringBuilder();
        empleadoInfo
                .append("@ClientNickname:crearEmpleado:")
                .append("documentoIdentidad=").append(docIdentidad).append(",")
                .append("primerNombre=").append(primerNombre).append(",")
                .append("segundoNombre=").append(segundoNombre).append(",")
                .append("primerApellido=").append(primerApellido).append(",")
                .append("segundoApellido=").append(segundoApellido).append(",")
                .append("fechaNacimiento=").append(fechaNacimiento).append(",")
                .append("estado=").append((estado) ? 1 : 0).append(",")
                .append("cargo=").append(cargo.getId()).append(",")
                .append("email=").append(email).append(",")
                .append("sueldo=").append(sueldo).append(",")
                .append("comision=").append(comisionEmpleado).append(",")
                .append("departamento=").append(departamento.getId()).append(",")
                .append("gerente=").append(gerente.getId());
        try {
            client.sendMessage(empleadoInfo.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Optional<Departamento> buscarDepartamentoPorNombre(String nombre) {
        return listadoDepartamentos.stream()
                .filter(departamento -> departamento.getNombre().equals(nombre))
                .findFirst();
    }

    public Optional<Cargo> buscarCargoPorNombre(String nombre) {
        return listadoCargos.stream()
                .filter(cargo -> cargo.getNombre().equals(nombre))
                .findFirst();
    }

    public Optional<Empleado> buscarGerentePorNombre(String nombre) {
        return listadoGerentes.stream()
                .filter(empleado -> empleado.getFullName().equals(nombre))
                .findFirst();
    }

    public void actualizarEmpleado(ActionEvent actionEvent) {
        String docIdentidad = docIdentidadEmpleado.getText();
        String primerNombre = primerNombreEmpleado.getText();
        String segundoNombre = segundoNombreEmpleado.getText();
        String primerApellido = primerApellidoEmpleado.getText();
        String segundoApellido = segundoApellidoEmpleado.getText();
        LocalDate fechaNacimiento = fechaNacimientoEmpleado.getValue();
        boolean estado = estadoEmpleado.isSelected();
        Cargo cargo = buscarCargoPorNombre(cargoEmpleado.getSelectionModel().getSelectedItem()).get();
        String email = emailEmpleado.getText();
        String sueldo = sueldoEmpleado.getText();
        String comisionEmpleado = comision.getText();
        Departamento departamento = buscarDepartamentoPorNombre(departamentoEmpleado.getSelectionModel().getSelectedItem()).get();
        Empleado gerente = buscarGerentePorNombre(gerenteEmpleado.getSelectionModel().getSelectedItem()).get();

        StringBuilder empleadoInfo = new StringBuilder();
        empleadoInfo
                .append("@ClientNickname:actualizarEmpleado:")
                .append("documentoIdentidad=").append(docIdentidad).append(",")
                .append("primerNombre=").append(primerNombre).append(",")
                .append("segundoNombre=").append(segundoNombre).append(",")
                .append("primerApellido=").append(primerApellido).append(",")
                .append("segundoApellido=").append(segundoApellido).append(",")
                .append("fechaNacimiento=").append(fechaNacimiento).append(",")
                .append("estado=").append((estado) ? 1 : 0).append(",")
                .append("cargo=").append(cargo.getId()).append(",")
                .append("email=").append(email).append(",")
                .append("sueldo=").append(sueldo).append(",")
                .append("comision=").append(comisionEmpleado).append(",")
                .append("departamento=").append(departamento.getId()).append(",")
                .append("gerente=").append(gerente.getId());
        try {
            client.sendMessage(empleadoInfo.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}