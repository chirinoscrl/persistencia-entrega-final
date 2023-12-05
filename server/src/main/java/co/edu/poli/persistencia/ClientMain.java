package co.edu.poli.persistencia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientMain {

    private final String serverAddress;
    private final int serverPort;

    public ClientMain(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void start() throws IOException {
        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            System.out.println("Running client");

            // Enviar el nickname al servidor.
            String nickname = "ClientNickname";
            out.println(nickname);

            // Enviar un comando SQL al servidor.
            String sqlCommand = "@ClientNickname@insert:INSERT INTO `recursos_humano_db`.`empleado` (`documento_identidad`, `primer_nombre`, `segundo_nombre`, `primer_apellido`, `segundo_apellido`, `email`, `fecha_nac`, `sueldo`, `cargo_id`, `departamento_id`) VALUES ('CC65327902', 'Rosanny', '', 'Chirinos', '', 'ross.chirinos@mail.com', '1980-05-22', 5000000, 1, 1)";
            System.out.println(sqlCommand);
            out.println(sqlCommand);

            // Leer la respuesta del servidor.
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("Server response: " + response);

                String[] rows = response.split(",");

                for (String row : rows) {
                    // Dividir cada fila en columnas
                    String[] columns = row.split("\\|");

                    //Empleado empleado = new Empleado(columns);
                    //System.out.println("Nombre del Empleado: " + empleado);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Cambia la dirección IP y el número de puerto para que coincidan con tu configuración del servidor.
        ClientMain client = new ClientMain("localhost", 8888);
        Thread.sleep(3000);
        client.start();
    }
}