package co.edu.poli.persistencia;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.*;
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
                return true;
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
                            case "@delete":
                                sqlResult = executeDelete(sql);
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

    private SqlResult buscarEmpleado(String sql) {
        logger.info("Buscar empleado: " + sql);
        Map<String, String> datos = getCamposDeString(sql);
        String buscarEmpleadoSql = String.format(
                "SELECT * FROM empleado WHERE documento_identidad = '%s'",
                datos.get("documentoIdentidad")
        );
        return executeSql(buscarEmpleadoSql);
    }

    private SqlResult executeSelect(String sql) {
        return executeSql(sql);
    }

    private SqlResult executeInsertEmpleado(String sql) {
        logger.info("Empleado a insertar: " + sql);
        Map<String, String> datos = getCamposDeString(sql);

        String insertSql = String.format(
                "INSERT INTO empleado (" +
                        "documento_identidad, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, " +
                        "email, fecha_nac, sueldo, cargo_id, departamento_id, gerente_id, estado, comision)" +
                        " VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, '%s', '%s', '%s', '%s', '%s')",
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

    private SqlResult executeDelete(String sql) {
        return executeSql(sql);
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