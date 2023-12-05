module co.edu.poli.persistencia.cliente {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.logging;
    requires org.apache.logging.log4j;


    opens co.edu.poli.persistencia.cliente to javafx.fxml;
    exports co.edu.poli.persistencia.cliente;
    exports co.edu.poli.persistencia.cliente.dto;
    opens co.edu.poli.persistencia.cliente.dto to javafx.fxml;
}