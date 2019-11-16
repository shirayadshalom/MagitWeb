
import components.main.MagitController;
import engine.Magit;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;

import java.net.URL;



public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {


        FXMLLoader loader = new FXMLLoader();


        // load main fxml
        URL mainFXML = getClass().getResource("Magit.fxml");
        loader.setLocation(mainFXML);
        ScrollPane root = loader.load();


        MagitController magitController = loader.getController();
        Magit magit=new Magit(magitController);
        magitController.setMagit(magit);
        magitController.setPrimaryStage(primaryStage);


        primaryStage.setTitle("Magit");
        Scene scene = new Scene(root, 1300, 643);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) throws Exception{
        launch(args);


    }
}
