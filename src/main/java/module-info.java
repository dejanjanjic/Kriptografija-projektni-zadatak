module net.etfbl.kriptografija {
    requires javafx.controls;
    requires javafx.fxml;


    opens net.etfbl.kriptografija to javafx.fxml;
    exports net.etfbl.kriptografija;
}