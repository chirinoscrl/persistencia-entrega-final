package co.edu.poli.persistencia.server;

import co.edu.poli.persistencia.server.dto.Empleado;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * La clase ClientThread representa un hilo de cliente que maneja la comunicación entre el servidor y un solo cliente.
 *
 * @author Autor
 * @version 1.0
 * @since 2023.11.06
 */
class ClientThread extends Thread {

    private static final Logger logger = Logger.getLogger(ClientThread.class);

    // La conexión del cliente
    private final Socket clientSocket;

    // La lista de clientes activos con su respectiva capa de escritura
    // PrintWriter se utiliza para enviar mensajes a los clientes conectados al servidor en una aplicación de múltiples hilos.
    private final ConcurrentHashMap<String, PrintWriter> activeClientsWriters;

    private String clientNickname;

    /**
     * Inicializa una nueva instancia de la clase ClientThread.
     *
     * @param clientSocket         El socket del cliente.
     * @param activeClientsWriters Un ConcurrentHashMap en el que la clave representa el nombre de usuario
     *                             y el valor el PrintWriter asociado a ese usuario.
     */
    public ClientThread(Socket clientSocket, ConcurrentHashMap<String, PrintWriter> activeClientsWriters) {
        this.clientSocket = clientSocket;
        this.activeClientsWriters = activeClientsWriters;
    }

    /**
     * Es invocado cuando se inicia un nuevo hilo de ejecución.
     */
    @Override
    public void run() {
        try {
            processClient();
        } catch (IOException e) {
            logger.info("An error occurred: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Procesa la comunicación con el cliente.
     *
     * @throws IOException Si ocurre un error de entrada/salida durante la comunicación con el cliente.
     */
    private void processClient() throws IOException {
        // Creando las capas de escritura y lectura para el cliente
        try (BufferedReader clientInputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter clientOutputWriter = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Inicializando el nickname del cliente
            clientNickname = clientInputReader.readLine();

            // Esta parte se encarga de verificar si el nickname ya está en uso
            if (isNicknameInUse(clientOutputWriter)) return;

            // Inicio del ciclo que maneja los mensajes del cliente
            handleClientMessage(clientInputReader, clientOutputWriter);
        }
    }

    /**
     * Chequea si un apodo se encuentra actualmente en uso.
     *
     * @param clientOutputWriter Es utilizado para enviar mensajes al cliente.
     * @return Verdadero si el apodo está en uso; falso en caso contrario.
     */
    private boolean isNicknameInUse(PrintWriter clientOutputWriter) {
        synchronized (activeClientsWriters) {
            if (activeClientsWriters.containsKey(clientNickname)) {
                clientOutputWriter.println("Nickname already in use. Disconnecting...");
                throw new RuntimeException("Nickname already in use. Disconnecting...");
            } else {
                activeClientsWriters.put(clientNickname, clientOutputWriter);
            }
        }
        return false;
    }

    /**
     * Recibe los mensajes del cliente.
     *
     * @param clientInputReader  El BufferedReader del cliente.
     * @param clientOutputWriter El PrintWriter del cliente.
     * @throws IOException si se presenta un error al leer desde cliente.
     */
    private void handleClientMessage(BufferedReader clientInputReader, PrintWriter clientOutputWriter) throws IOException {
        String clientMessage;
        while ((clientMessage = clientInputReader.readLine()) != null) {
            logger.info("Client [" + clientNickname + "]: " + clientMessage);
            if (clientMessage.equalsIgnoreCase("chao")) {
                break; // salir del bucle y terminar la conexión
            } else if (clientMessage.startsWith("@")) {
                if (clientMessage.split(":")[1].trim().equalsIgnoreCase("chao")) {

                    logger.info("Client [" + clientNickname + "]: Conexion finalizada");
                    break;
                } else {
                    String[] splitStr = clientMessage.split(":", 3);  // Divide el string en tres partes

                    String clientNickname = splitStr[0];
                    String operation = splitStr[1];
                    String sql = splitStr[2];
                    logger.info("Client [" + clientNickname + "] -> query:" + sql);
                    try {
                        SqlResult sqlResult;

                        switch (operation) {
                            case "buscarEmpleado":
                                sqlResult = buscarEmpleado(sql);
                                String mensaje = "buscarEmpleado:" + sqlResult.getResultsList();
                                logger.info("Enviando mensaje" + mensaje);
                                clientOutputWriter.println(mensaje);
                                break;
                            case "obtenerListadoCargos":
                                sqlResult = executeSql("select * from cargo");
                                clientOutputWriter.println("obtenerListadoCargos:" + sqlResult.getResultsList());
                                break;
                            case "obtenerListadoDepartamentos":
                                sqlResult = executeSql("select * from departamento");
                                clientOutputWriter.println("obtenerListadoDepartamentos:" + sqlResult.getResultsList());
                                break;
                            case "obtenerListadoGerentes":
                                sqlResult = executeSql("select * from empleado where departamento_id =" + sql);
                                clientOutputWriter.println("obtenerListadoGerentes:" + sqlResult.getResultsList());
                                break;
                            case "crearEmpleado":
                                sqlResult = executeInsertEmpleado(sql);
                                logger.info("Result crearEmpleado: " + sqlResult.getUpdateCount());
                                clientOutputWriter.println("crearEmpleado:" + sqlResult.getUpdateCount());
                                break;
                            case "actualizarEmpleado":
                                sqlResult = actualizarEmpleado(sql);
                                logger.info("Result actualizarEmpleado: " + sqlResult.getUpdateCount());
                                clientOutputWriter.println("actualizarEmpleado:" + sqlResult.getUpdateCount());
                                if (sqlResult.getUpdateCount() == 1) {
                                    SqlResult sqlResultBusqueda = buscarEmpleado(sql);
                                    String mensajeBusqueda = "buscarEmpleado:" + sqlResultBusqueda.getResultsList();
                                    logger.info("Enviando mensaje" + mensajeBusqueda);
                                    clientOutputWriter.println(mensajeBusqueda);
                                }
                                break;
                            case "eliminarEmpleado":
                                sqlResult = executeEliminarEmpleado(sql);
                                logger.info("Result eliminarEmpleado: " + sqlResult.getUpdateCount());
                                clientOutputWriter.println("eliminarEmpleado:" + sqlResult.getUpdateCount());
                                if (sqlResult.getUpdateCount() == 1) {
                                    SqlResult sqlResultBusqueda = buscarEmpleado(sql);
                                    String mensajeBusqueda = "buscarEmpleado:" + sqlResultBusqueda.getResultsList();
                                    logger.info("Enviando mensaje" + mensajeBusqueda);
                                    clientOutputWriter.println(mensajeBusqueda);
                                }
                                break;
                            case "historicoEmpleado":
                                sqlResult = buscarHistoricoEmpleado(sql);
                                String historicoEmpleadoMensaje = "historicoEmpleado:" + sqlResult.getResultsList();
                                logger.info("Enviando mensaje" + historicoEmpleadoMensaje);
                                clientOutputWriter.println(historicoEmpleadoMensaje);
                                break;
                            default:
                                throw new IllegalArgumentException("Invalid SQL operation: " + sql);
                        }
                    } catch (IllegalArgumentException e) {
                        logger.error("An error occurred: " + e.getMessage());
                        clientOutputWriter.println("An error occurred: " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Busca un empleado en la base de datos según el SQL proporcionado.
     *
     * @param sql el SQL para buscar al empleado.
     * @return un objeto SqlResult con los resultados de la búsqueda.
     */
    private SqlResult buscarEmpleado(String sql) {
        logger.info("Buscar empleado: " + sql);
        Map<String, String> datos = getCamposDeString(sql);
        String buscarEmpleadoSql = String.format(
                "SELECT * FROM empleado WHERE documento_identidad = '%s'",
                datos.get("documentoIdentidad")
        );
        return executeSql(buscarEmpleadoSql);
    }

    /**
     * Busca el historial de un empleado en la base de datos según el SQL proporcionado.
     *
     * @param sql el SQL para buscar el historial del empleado.
     * @return un objeto SqlResult con los resultados de la búsqueda del historial.
     */
    private SqlResult buscarHistoricoEmpleado(String sql) {
        logger.info("Buscar historico empleado: " + sql);
        Map<String, String> datos = getCamposDeString(sql);
        String buscarHistoricoEmpleadoSql = String.format(
                "select * from historico" +
                        " inner join departamento departamento on historico.departamento_id = departamento.id" +
                        " inner join localizacion localizacion on departamento.localizacion_id = localizacion.id" +
                        " inner join ciudad ciudad on ciudad.id = localizacion.ciudad_id" +
                        " inner join cargo cargo on cargo.id = historico.cargo_id" +
                        " where empleado_id = %s;",
                datos.get("empleadoId")
        );
        return executeSql(buscarHistoricoEmpleadoSql);
    }

    /**
     * Ejecuta una sentencia SQL para insertar un empleado en la base de datos.
     *
     * @param sql el SQL para insertar el empleado.
     * @return un objeto SqlResult con los resultados de la ejecución de la sentencia SQL.
     */
    private SqlResult executeInsertEmpleado(String sql) {
        logger.info("Empleado a insertar: " + sql);
        Map<String, String> datos = getCamposDeString(sql);

        String insertSql = String.format(
                "INSERT INTO empleado (" +
                        "documento_identidad, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, " +
                        "email, fecha_nac, sueldo, cargo_id, departamento_id, gerente_id, estado, comision)" +
                        " VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s, %s, '%s')",
                datos.get("documentoIdentidad"),
                datos.get("primerNombre"),
                datos.get("segundoNombre"),
                datos.get("primerApellido"),
                datos.get("segundoApellido"),
                datos.get("email"),
                datos.get("fechaNacimiento"),
                datos.get("sueldo"),
                datos.get("cargo"),
                datos.get("departamento"),
                datos.get("gerente"),
                datos.get("estado"),
                datos.get("comision")
        );

        return executeSql(insertSql);
    }

    /**
     * Retorna un mapa que contiene los campos y valores extraídos de una cadena de texto.
     *
     * @param sql la cadena de texto que contiene los campos y valores en formato clave=valor.
     * @return un mapa que contiene los campos y valores extraídos de la cadena de texto.
     */
    private static Map<String, String> getCamposDeString(String sql) {
        Map<String, String> datos = new HashMap<>();

        String key = "";
        StringTokenizer st = new StringTokenizer(sql, "=,", true);
        String prevToken = "";
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (!token.equals("=") && !token.equals(",")) {
                if (prevToken.equals("=")) {
                    datos.put(key, token);
                } else {
                    key = token;
                }
            }
            prevToken = token;
        }
        return datos;
    }

    /**
     * Actualiza los datos de un empleado en la base de datos.
     *
     * @param sql la cadena de texto que contiene los campos y valores a actualizar en formato clave=valor.
     * @return un objeto de tipo SqlResult que contiene el resultado de la ejecución de la sentencia SQL.
     */
    private SqlResult actualizarEmpleado(String sql) {
        logger.info("Empleado a actualizar: " + sql);
        Map<String, String> datos = getCamposDeString(sql);

        String updateSql = String.format(
                "UPDATE empleado SET " +
                        "documento_identidad = '%s', " +
                        "primer_nombre = '%s', " +
                        "segundo_nombre = '%s', " +
                        "primer_apellido = '%s', " +
                        "segundo_apellido = '%s', " +
                        "email = '%s', " +
                        "fecha_nac = '%s', " +
                        "sueldo = %s, " +
                        "cargo_id = %s, " +
                        "departamento_id = %s, " +
                        "gerente_id = %s, " +
                        "estado = %s , " +
                        "comision = %s " +
                        "WHERE documento_identidad = '%s'",
                datos.get("documentoIdentidad"),
                datos.get("primerNombre"),
                datos.get("segundoNombre"),
                datos.get("primerApellido"),
                datos.get("segundoApellido"),
                datos.get("email"),
                datos.get("fechaNacimiento"),
                datos.get("sueldo"),
                datos.get("cargo"),
                datos.get("departamento"),
                datos.get("gerente"),
                datos.get("estado"),
                datos.get("comision"),
                datos.get("documentoIdentidad")
        );

        return executeSql(updateSql);
    }

    /**
     * Ejecuta la eliminación de un empleado en la base de datos.
     *
     * @param sql la cadena de texto que contiene los campos y valores necesarios para la eliminación.
     * @return un objeto de tipo SqlResult que contiene el resultado de la ejecución de la sentencia SQL.
     */
    private SqlResult executeEliminarEmpleado(String sql) {
        logger.info("Empleado a eliminar: " + sql);
        Map<String, String> datos = getCamposDeString(sql);
        // Cambia estos valores por los de tu configuración
        String url = "jdbc:mysql://localhost/recursos_humano_db";
        String username = "root";
        String password = "root";
        SqlResult sqlResult = new SqlResult();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                // 1. Buscar el empleado
                String buscarEmpleadoSql = String.format("SELECT * FROM empleado WHERE documento_identidad = '%s'", datos.get("documentoIdentidad"));
                logger.info("Sentencia a ejecutar:" + buscarEmpleadoSql);
                ResultSet resultSet = statement.executeQuery(buscarEmpleadoSql);
                if (!resultSet.next()) {
                    throw new RuntimeException("Empleado no encontrado");
                }
                // Recuperar los detalles del empleado si se necesitan para las siguientes operaciones
                String[] datosEmpleado = getDatosEmpleado(resultSet);
                Empleado empleado = new Empleado(datosEmpleado);
                // 2. Actualizar empleado
                String actualizarEmpleadoSql = String.format("UPDATE empleado SET estado = %s WHERE documento_identidad = '%s'", false, datos.get("documentoIdentidad"));
                logger.info("Sentencia a ejecutar:" + actualizarEmpleadoSql);
                int updatedRows = statement.executeUpdate(actualizarEmpleadoSql);
                if (updatedRows != 1) {
                    throw new RuntimeException("Error al actualizar el estado del empleado");
                }
                // 3. Insertar en historico
                LocalDate fechaRetiro = LocalDate.now();
                String insertHistoricoSql = String.format(
                        "INSERT INTO historico (empleado_id, cargo_id, departamento_id, fecha_retiro) VALUES ('%s', '%s', '%s', '%s')",
                        empleado.getId(),
                        empleado.getCargoId(),
                        empleado.getDepartamentoId(),
                        fechaRetiro);
                logger.info("Sentencia a ejecutar:" + insertHistoricoSql);
                statement.executeUpdate(insertHistoricoSql);
                connection.commit();
                sqlResult.setUpdateCount(updatedRows); //or something else that indicates success

            } catch (Exception ex) {
                connection.rollback();
                logger.error(ex.getMessage(), ex);
                sqlResult.setException(new SQLException(ex));
            }

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            sqlResult.setException(e);
        }

        return sqlResult;
    }

    /**
     * Recupera los datos de un empleado a partir de un ResultSet.
     *
     * @param resultSet el ResultSet que contiene los datos del empleado.
     * @return un array de Strings con los datos del empleado.
     * @throws SQLException si ocurre un error al acceder a los datos del ResultSet.
     */
    private static String[] getDatosEmpleado(ResultSet resultSet) throws SQLException {
        String[] datosEmpleado = new String[14];
        datosEmpleado[0] = String.valueOf(resultSet.getInt("id"));
        datosEmpleado[1] = resultSet.getString("documento_identidad");
        datosEmpleado[2] = resultSet.getString("primer_nombre");
        datosEmpleado[3] = resultSet.getString("segundo_nombre");
        datosEmpleado[4] = resultSet.getString("primer_apellido");
        datosEmpleado[5] = resultSet.getString("segundo_apellido");
        datosEmpleado[6] = resultSet.getString("email");
        datosEmpleado[7] = resultSet.getDate("fecha_nac").toString(); //suponiendo que fecha_nac es yyyy-mm-dd formato
        datosEmpleado[8] = resultSet.getBigDecimal("sueldo").toString();
        datosEmpleado[9] = String.valueOf(resultSet.getInt("comision"));
        datosEmpleado[10] = String.valueOf(resultSet.getInt("cargo_id"));
        datosEmpleado[11] = String.valueOf(resultSet.getInt("departamento_id"));
        datosEmpleado[12] = String.valueOf(resultSet.getInt("gerente_id"));
        datosEmpleado[13] = String.valueOf(resultSet.getBoolean("estado"));
        return datosEmpleado;
    }

    /**
     * Ejecuta una consulta SQL en la base de datos.
     *
     * @param sql la consulta SQL a ejecutar.
     * @throws SQLException si ocurre un error de base de datos.
     */
    private SqlResult executeSql(String sql) {
        SqlResult sqlResult = new SqlResult();

        // Cambia estos valores por los de tu configuración
        String url = "jdbc:mysql://localhost/recursos_humano_db";
        String username = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            logger.info("Sentencia a ejecutar:" + sql);
            boolean isResultSet = statement.execute(sql);
            if (isResultSet) {
                ResultSet resultSet = statement.getResultSet();
                List<String> results = new ArrayList<>();
                ResultSetMetaData md = resultSet.getMetaData();
                int columns = md.getColumnCount();
                while (resultSet.next()) {
                    StringBuilder row = new StringBuilder();
                    for (int i = 1; i <= columns; ++i) {
                        row.append(resultSet.getObject(i)).append("|");
                    }
                    results.add(row.toString());
                }
                sqlResult.setResults(results);
            } else {
                sqlResult.setUpdateCount(statement.getUpdateCount());
            }

            logger.info("Consulta " + sql + " ejecutada con exito");
        } catch (SQLException e) {
            logger.error(e);
            sqlResult.setException(e);
        }

        return sqlResult;
    }
}