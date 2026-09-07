module org.example.dailymeal {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.xml.bind;

    opens org.example.dailymeal.PresentationLayer to javafx.fxml;
    opens org.example.dailymeal.DataAccessLayer to jakarta.xml.bind;
    opens org.example.dailymeal.BusinessLayer to javafx.base;

    exports org.example.dailymeal;
    exports org.example.dailymeal.PresentationLayer;
}