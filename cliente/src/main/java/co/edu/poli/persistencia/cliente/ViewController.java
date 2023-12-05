package co.edu.poli.persistencia.cliente;

import co.edu.poli.persistencia.cliente.dto.Cargo;
import co.edu.poli.persistencia.cliente.dto.Departamento;
import co.edu.poli.persistencia.cliente.dto.Empleado;
import co.edu.poli.persistencia.cliente.dto.Historico;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private Label statusConexion;

    @FXML
    private Label statusOperacion;

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
    private List<Historico> historicoEmpleados;

    @FXML
    private TableColumn<Historico, String> fechaRetiroHistorico;
    @FXML
    private TableColumn<Historico, String> cargoHistorico;
    @FXML
    private TableColumn<Historico, String> DepartamentoHistorico;
    @FXML
    private TableColumn<Historico, String> LocalizacionHistorico;
    @FXML
    private TableColumn<Historico, String> CiudadHistorico;
    @FXML
    private TableView<Historico> historico;
    private ObservableList<Historico> listaHistoricos;

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

        fechaRetiroHistorico.setCellValueFactory(new PropertyValueFactory<>("fechaRetiro"));
        cargoHistorico.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        DepartamentoHistorico.setCellValueFactory(new PropertyValueFactory<>("departamento"));
        LocalizacionHistorico.setCellValueFactory(new PropertyValueFactory<>("localizacion"));
        CiudadHistorico.setCellValueFactory(new PropertyValueFactory<>("ciudad"));

        listaHistoricos = FXCollections.observableArrayList();
        historico.setItems(listaHistoricos);

        // Calculamos las fechas de límite (18 años atrás y 70 años atrás desde hoy)
        LocalDate maxDate = LocalDate.now().minusYears(18); // Personas mayores de 18 años
        LocalDate minDate = LocalDate.now().minusYears(70); // Personas menores de 70 años

        // Añadimos un listener para la fecha seleccionada en el DatePicker
        fechaNacimientoEmpleado.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue != null &&
                    (newValue.isAfter(maxDate) || newValue.isBefore(minDate))) {
                // Si la fecha seleccionada está fuera del rango permitido,
                // regresamos el valor del DatePicker a la fecha anterior válida
                fechaNacimientoEmpleado.setValue(oldValue);
            }
        });

        // Configuramos la fecha inicial en el DatePicker a nuestra fecha máxima permitida
        fechaNacimientoEmpleado.setValue(maxDate);
    }

    /**
     * Este método se utiliza para establecer una conexión con el servidor.
     * @param actionEvent El evento del botón de conexión.
     */
    public void onConexion(ActionEvent actionEvent) {
        try {
            nickname = nicknameInput.getText();
            client = new ClientConnect(nickname,
                    cargos -> Platform.runLater(() -> obtenerListadoCargos(cargos)),
                    departamentos -> Platform.runLater(() -> obtenerListadoDepartamentos(departamentos)),
                    gerentes -> Platform.runLater(() -> obtenerListadoGerentes(gerentes)),
                    empleado -> Platform.runLater(() -> obtenerBusquedaEmpleado(empleado)),
                    actualizadoEmpleado -> Platform.runLater(() -> actualizadoEmpleado(actualizadoEmpleado)),
                    historicoEmpleado -> Platform.runLater(() -> historicoEmpleado(historicoEmpleado)),
                    creacionEmpleado -> Platform.runLater(() -> creacionEmpleado(creacionEmpleado))
            );

            // Deshabilita el botón de conexión y cambia la etiqueta de estado después de la conexión
            // connectButton.setDisable(true);
            statusConexion.setText("Usuario conectado");
            //chatArea.appendText(String.format("Te acabas de conectar...\n"));

            new Thread(() -> {
                while (true) {
                    try {
                        client.readMessages();
                    } catch (IOException e) {
                        statusConexion.setText("Falló la conexión");
                    }
                }
            }).start();
        } catch (Exception e) {
            // Revisa el error, habilita el botón de conexión y muestra el error en la etiqueta de estado si la conexión falla
            // connectButton.setDisable(false);
            statusConexion.setText("Falló la conexión: " + e.getMessage());
            logger.error(e);
        }
    }

    /**
     * Este método se utiliza para actualizar un empleado en base a un resultado obtenido del servidor.
     *
     * @param actualizadoEmpleado El resultado de la actualización del empleado.
     */
    private void actualizadoEmpleado(String actualizadoEmpleado) {
        Platform.runLater(() -> {
            System.out.println("Resultado de actualizacion:" + actualizadoEmpleado);
            if (actualizadoEmpleado.equals("1")) {
                statusOperacion.setText("Empleado actualizado con exito");
            } else {
                statusOperacion.setText("Empleado no puso ser actualizado");
            }
        });
    }

    /**
     * Este método se utiliza para manejar el resultado de la creación de un empleado.
     *
     * @param creacionEmpleado El resultado de la creación del empleado.
     */
    private void creacionEmpleado(String creacionEmpleado) {
        Platform.runLater(() -> {
            System.out.println("Resultado de creacion:" + creacionEmpleado);
            if (creacionEmpleado.equals("1")) {
                statusOperacion.setText("Empleado creado con exito");
            } else {
                statusOperacion.setText("Empleado no puso ser creado");
            }
        });
    }

    /**
     * Este método se utiliza para obtener y manejar la búsqueda de un empleado.
     *
     * @param empleadoStr La cadena que contiene los datos del empleado buscado.
     */
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

                String buscarHistoricoMensaje = "@ClientNickname:historicoEmpleado:empleadoId=" + empleadoEncontrado.getId();
                try {
                    client.sendMessage(buscarHistoricoMensaje);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                limpiarCampos();
            }
        });
    }

    /**
     * Este método se utiliza para encontrar y seleccionar el cargo de un empleado.
     *
     * @param cargoId El ID del cargo que se quiere encontrar y seleccionar.
     */
    private void encontrarYSeleccionarCargo(int cargoId) {
        for (Cargo cargo : listadoCargos) {
            if (cargo.getId() == cargoId) {
                cargoEmpleado.getSelectionModel().select(cargo.getNombre());
                break;
            }
        }
    }

    /**
     * Este método se utiliza para encontrar y seleccionar el departamento de un empleado.
     *
     * @param departamentoId El ID del departamento que se quiere encontrar y seleccionar.
     */
    private void encontrarYSeleccionarDepartamento(int departamentoId) {
        for (Departamento departamento : listadoDepartamentos) {
            if (departamento.getId() == departamentoId) {
                departamentoEmpleado.getSelectionModel().select(departamento.getNombre());
                break;
            }
        }
    }

    /**
     * Este método se utiliza para buscar empleados a través de su documento de identidad.
     *
     * @param actionEvent El evento que dispara la búsqueda de empleados.
     */
    public void onBuscarEmpleados(ActionEvent actionEvent) {
        String documentoEmpleadoBuscar = documentoEmpleadoBuscarField.getText();
        String sqlCommand = "@ClientNickname:buscarEmpleado:documentoIdentidad=" + documentoEmpleadoBuscar;

        try {
            client.sendMessage(sqlCommand);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Este método se utiliza para obtener un listado de cargos a partir de una cadena de texto.
     *
     * @param cargosStr La cadena de texto que contiene la información de los cargos.
     */
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

    /**
     * Este método se utiliza para obtener un listado de departamentos a partir de una cadena de texto.
     *
     * @param departamentosStr La cadena de texto que contiene la información de los departamentos.
     */
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

    /**
     * Este método se utiliza para manejar el evento de cambio de departamento.
     *
     * @param actionEvent El objeto de evento que desencadena el cambio de departamento.
     */
    public void handleChangeDepartamento(ActionEvent actionEvent) {
        String selectedItems = departamentoEmpleado.getSelectionModel().getSelectedItem();
        System.out.println("Selected item: " + selectedItems);

        if (selectedItems != null) {
            Departamento departamentoId = buscarDepartamentoPorNombre(selectedItems).get();
            String msg = "@ClientNickname:obtenerListadoGerentes:" + departamentoId.getId();

            try {
                client.sendMessage(msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Este método se utiliza para obtener el listado de gerentes.
     *
     * @param gerentesStr Una cadena que representa el listado de gerentes.
     */
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

                if (resultadoBusquedaEmpleado != null && !Objects.equals(resultadoBusquedaEmpleado.get(0).getGerenteId(), "")) {
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

    /**
     * Este método se utiliza para crear un nuevo empleado.
     *
     * @param actionEvent El evento de acción que activó el método.
     */
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
                .append("departamento=").append(departamento.getId()).append(",");

        if (gerenteEmpleado.getSelectionModel().getSelectedItem() != null) {
            Empleado gerente = buscarGerentePorNombre(gerenteEmpleado.getSelectionModel().getSelectedItem()).get();
            empleadoInfo.append("gerente=").append(gerente.getId());
        } else {
            empleadoInfo.append("gerente=");
        }

        try {
            client.sendMessage(empleadoInfo.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Este método se utiliza para buscar un departamento por su nombre.
     *
     * @param nombre El nombre del departamento que se busca.
     * @return Un Optional que contiene el departamento si se encuentra, o un Optional vacío si no se encuentra.
     */
    public Optional<Departamento> buscarDepartamentoPorNombre(String nombre) {
        return listadoDepartamentos.stream()
                .filter(departamento -> departamento.getNombre().equals(nombre))
                .findFirst();
    }

    /**
     * Este método se utiliza para buscar un cargo por su nombre.
     *
     * @param nombre El nombre del cargo que se busca.
     * @return Un Optional que contiene el cargo si se encuentra, o un Optional vacío si no se encuentra.
     */
    public Optional<Cargo> buscarCargoPorNombre(String nombre) {
        return listadoCargos.stream()
                .filter(cargo -> cargo.getNombre().equals(nombre))
                .findFirst();
    }

    /**
     * Este método se utiliza para buscar un gerente por su nombre.
     *
     * @param nombre El nombre del gerente que se busca.
     * @return Un Optional que contiene el gerente si se encuentra, o un Optional vacío si no se encuentra.
     */
    public Optional<Empleado> buscarGerentePorNombre(String nombre) {
        return listadoGerentes.stream()
                .filter(empleado -> empleado.getFullName().equals(nombre))
                .findFirst();
    }

    /**
     * Este método se utiliza para actualizar la información de un empleado.
     *
     * @param actionEvent El evento de acción que desencadena la actualización del empleado.
     */
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
                .append("departamento=").append(departamento.getId()).append(",");

        if (gerenteEmpleado.getSelectionModel().getSelectedItem() != null) {
            Empleado gerente = buscarGerentePorNombre(gerenteEmpleado.getSelectionModel().getSelectedItem()).get();
            empleadoInfo.append("gerente=").append(gerente.getId());
        } else {
            empleadoInfo.append("gerente=");
        }

        System.out.println("Actualizando el usuario:"+ empleadoInfo.toString());
        try {
            client.sendMessage(empleadoInfo.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Este método se utiliza para limpiar todos los campos del formulario de empleado.
     */
    public void limpiarCampos() {
        docIdentidadEmpleado.clear();
        primerNombreEmpleado.clear();
        segundoNombreEmpleado.clear();
        primerApellidoEmpleado.clear();
        segundoApellidoEmpleado.clear();
        emailEmpleado.clear();
        fechaNacimientoEmpleado.setValue(null);
        estadoEmpleado.setSelected(true);
        cargoEmpleado.setValue(null);
        sueldoEmpleado.clear();
        comision.clear();
        departamentoEmpleado.setValue(null);
        gerenteEmpleado.setValue(null);
        listaHistoricos.clear();
    }

    /**
     * Este método se utiliza para eliminar un empleado.
     *
     * @param actionEvent El evento que desencadena la acción de eliminar empleado.
     */
    public void eliminarEmpleado(ActionEvent actionEvent) {
        String docIdentidad = docIdentidadEmpleado.getText();
        StringBuilder empleadoInfo = new StringBuilder();
        empleadoInfo
                .append("@ClientNickname:eliminarEmpleado:")
                .append("documentoIdentidad=").append(docIdentidad);
        try {
            client.sendMessage(empleadoInfo.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Este método se utiliza para actualizar el historial de empleados.
     *
     * @param historicoStr String que contiene la información del historial de empleados.
     */
    public void historicoEmpleado(String historicoStr) {
        Platform.runLater(() -> {
            System.out.println(historicoStr);
            historicoEmpleados = new ArrayList<>();
            listaHistoricos.clear();
            String[] historicosStrings = historicoStr.substring(1, historicoStr.length() - 1).split(",");
            if (!historicosStrings[0].equals("") && !historicoStr.equals("null")) {
                for (String historicoString : historicosStrings) {
                    String[] properties = historicoString.split("\\|");
                    Historico historico = new Historico(properties);
                    historicoEmpleados.add(historico);
                    listaHistoricos.add(historico);
                }
            }
        });
    }
}