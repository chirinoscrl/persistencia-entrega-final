package co.edu.poli.persistencia;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
                    break;
                }
            }
        }
    }
}