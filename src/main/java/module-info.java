//module org.example.dailymeal {
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires jakarta.xml.bind;
//    requires java.desktop;
//
//    exports org.example.dailymeal;
//
//    opens org.example.dailymeal.ui to javafx.graphics;
//}
module org.example.dailymeal {
    requires javafx.controls;
    requires javafx.fxml;

    // Экспортируем главный класс, чтобы JavaFX мог его запустить
    exports org.example.dailymeal;

    // ВАЖНО: Открываем пакет с контроллером для JavaFX FXML
    opens org.example.dailymeal.ui to javafx.fxml;

    // Если контроллер использует графику (Circle, Color и т.д.),
    // то также нужно открыть его для javafx.graphics:

}