package org.example.dailymeal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jakarta.xml.bind.JAXBException;
import org.example.dailymeal.DataAccessLayer.Db;
import org.example.dailymeal.ServiceLayer.impl.CatalogService;
import java.io.IOException;

import static org.example.dailymeal.ParserXmlToJavaObjects.ParseXmlToJavaObject;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException, JAXBException {
        String xmlPath = "C:\\Users\\User\\Desktop\\DailyMeal\\src\\main\\resources\\FoodProducts.xml";
        Db db = ParseXmlToJavaObject(xmlPath);

        CatalogService catalogService = new CatalogService();
        catalogService.setDb(db);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainStage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Daily Meal Planner");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        stage.show();
    }
}