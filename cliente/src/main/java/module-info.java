module co.edu.poli.persistencia.cliente {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.edu.poli.persistencia.cliente to javafx.fxml;
    exports co.edu.poli.persistencia.cliente;
}