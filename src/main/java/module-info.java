module org.example.Perlinnoise {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;

    opens org.example to javafx.fxml;
    exports org.example;
}
