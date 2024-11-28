package de.uulm.in.vs.grn.p3a;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;



public class SockagramUI extends Application{

    @Override
    public void start(Stage stage){
        StackPane stackPane = new StackPane();
        Label l = new Label("Hello, JavaFX ");
        Button b = new Button("test");
        ObservableList list = stackPane.getChildren();
        list.addAll(b,l);

        Scene scene = new Scene(stackPane, 640, 480);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
