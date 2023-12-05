package co.edu.poli.persistencia.cliente;

import co.edu.poli.persistencia.cliente.dto.Cargo;
import co.edu.poli.persistencia.cliente.dto.Empleado;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * La clase ChatClient es responsable de establecer una conexión con un servidor de chat y manejar los mensajes entrantes y salientes.
 *
 * @author Autor
 * @version 1.0
 * @since 2023.11.06
 */
public class ClientConnect {

    private final Socket socket;
    private final BufferedReader buffReader;
    private final PrintWriter buffWriter;

    private final Consumer<String> cargosHandler;

    private final Consumer<String> departamentosHandler;

    private final Consumer<String> gerentesHandler;

    /**
     * Inicializa una nueva instancia de la clase ChatClient.
     *
     * @param nickname      El apodo que se utilizará para el usuario actual.
     * @param cargosHandler        Una función Consumer que manejará las actualizaciones de usuarios activos en el chat.
     * @param departamentosHandler Una función Consumer que manejará los mensajes entrantes del chat.
     * @throws IOException Si hay un error de E/S al conectarse con el servidor.
     */
    public ClientConnect(String nickname,
                         Consumer<String> cargosHandler,
                         Consumer<String> departamentosHandler,
                         Consumer<String> gerentesHandler
    ) throws IOException {
        socket = new Socket("localhost", 8888);
        buffWriter = new PrintWriter(socket.getOutputStream(), true);
        buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        buffWriter.println(nickname);
        this.cargosHandler = cargosHandler;
        buffWriter.println("@ClientNickname:obtenerListadoCargos:");
        this.departamentosHandler = departamentosHandler;
        buffWriter.println("@ClientNickname:obtenerListadoDepartamentos:");
        this.gerentesHandler = gerentesHandler;
        System.out.println("Connected to server.");
    }

    /**
     * Devuelve si el socket está actualmente conectado.
     *
     * @return verdadero si el socket está conectado, falso en caso contrario.
     */
    public boolean isConnected() {
        return socket.isConnected();
    }

    /**
     * Enviar un mensaje utilizando la conexión establecida.
     *
     * @param msg el mensaje a enviar
     * @throws IOException si ocurre un error en el envío del mensaje.
     */
    public void sendMessage(String msg) throws IOException {
        buffWriter.println(msg);
        System.out.println("Sent message: " + msg);
    }

    /**
     * Lee los mensajes de texto enviados por el servidor y realiza diferentes acciones según el contenido del mensaje.
     *
     * @throws IOException si ocurre un error durante la lectura de los mensajes.
     */
    public void readMessages() throws IOException {
        String serverMsg;
        while ((serverMsg = buffReader.readLine()) != null) {
            System.out.println(serverMsg);
            if (serverMsg.startsWith("Active Users: ")) {
                cargosHandler.accept(serverMsg.substring(14));
            }

            if (serverMsg.startsWith("todosEmpleados:")) {
                String[] rows = serverMsg.substring(1, serverMsg.length() - 1).split(",");

                if (!rows[0].equals("")) {
                    for (String row : rows) {
                        // Dividir cada fila en columnas
                        String[] columns = row.split("\\|");

                        Empleado empleado = new Empleado(columns);
                        System.out.println("Nombre del Empleado: " + empleado);
                    }
                }
            }

            if (serverMsg.startsWith("obtenerListadoCargos:")) {
                cargosHandler.accept(serverMsg.split(":")[1]); // Procesa el mensaje
            }

            if (serverMsg.startsWith("obtenerListadoDepartamentos:")) {
                departamentosHandler.accept(serverMsg.split(":")[1]); // Procesa el mensaje
            }

            if (serverMsg.startsWith("obtenerListadoGerentes:")) {
                gerentesHandler.accept(serverMsg.split(":")[1]); // Procesa el mensaje
            }
        }
    }
}