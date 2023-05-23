package tuki.diploma.tmo;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tuki.diploma.tmo.controllers.MainController;

public class TMOApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader mainLoader = new FXMLLoader(
                TMOApplication.class.getResource("main-view.fxml"));
        Parent root = mainLoader.load();

        MainController mainController = mainLoader.getController();
        mainController.setPrimaryStage(stage);

        Scene scene = new Scene(root, 620, 480);
        stage.setTitle("Take Me Out");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
