package de.uulm.in.vs.grn.p3a;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class SockagramUI extends Application{

    private Path imagePath;
    private byte[] file;
    private SockagramClient sock;
    //Ich entschuldige mich für diesen komischen code aber ich hab keine ahnung wie man javafx project richtig aufbaut
    //also wird es einfach so gemacht wie ich es irgendwie zum Funktionieren bringen kann, auch wenn das niemals intended ist
    @Override
    public void start(Stage stage){
        setConnectScene(stage);
    }
    private void setConnectScene(Stage stage){
        HBox hbox = new HBox();
        Button connectButton = new Button("Connect To Server");
        TextField serverField = new TextField("vns.lxd-vs.uni-ulm.de");
        TextField portField  = new TextField("7777");
        portField.setMaxWidth(100);
        EventHandler<ActionEvent> connectButtonEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                try {
                    sock = new SockagramClient(serverField.getText(),Integer.parseInt(portField.getText()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                setSendScene(stage);
            }
        };
        connectButton.setOnAction(connectButtonEvent);

        hbox.getChildren().addAll(serverField,portField,connectButton);
        hbox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(hbox,640,480);
        stage.setScene(scene);
        stage.show();
    }
    private void setSendScene(Stage stage){
        HBox hbox = new HBox();
        VBox vBox = new VBox(hbox);
        Label l = new Label("Choose your Image: ");
        Button selectButton = new Button("Select Image");
        Button sendButton = new Button("Send Image to Server");

        ComboBox comboBox = new ComboBox();
        comboBox.getItems().addAll("NO Filter","Black White","Eightbit","YOLO","SWAG(dreht den Swag auf jo)","Summer","Sepia");



        //idk what im doing if im being honest this uses way to many global variables to be good code ngl
        EventHandler<ActionEvent> selectButtonEvent =  new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                getPath(stage);
                try {
                    loadPic(imagePath);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                l.setText("Selected Image: " + imagePath.getFileName() + "  ");
                selectButton.setText("Change Image");
                //man hätte gleich mit dem InputStream arbeiten können aber hab das schon alles mit byte[] gemacht ich schreib das jetzt nicht um
                InputStream inputStream = new ByteArrayInputStream(file);
                vBox.getChildren().add(new ImageView(new Image(inputStream)));
                vBox.getChildren().addAll(comboBox,sendButton);
            }
        };
        EventHandler<ActionEvent> sendButtonEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                try {
                    sock.sendImage((byte) comboBox.getSelectionModel().getSelectedIndex(), file);
                    setImageRecivedScene(stage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        selectButton.setOnAction(selectButtonEvent);
        sendButton.setOnAction(sendButtonEvent);

        hbox.getChildren().addAll(l, selectButton);
        hbox.setAlignment(Pos.TOP_CENTER);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox, 800, 600);
        stage.setScene(scene);
        stage.show();

    }
    //TODO: Recive the image and Display it
    private void setImageRecivedScene(Stage stage) throws IOException{
        VBox vbox = new VBox();
        Label l = new Label("Image Received :)");
        ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(sock.getImageResponse())));

        vbox.getChildren().addAll(l,imageView);
        Scene scene = new Scene(null,640,480);
        stage.setScene(scene);
        stage.show();
    }
    //:( 
    private void getPath(Stage stage){
        FileChooser fileChooser = new FileChooser();
        this.imagePath =  fileChooser.showOpenDialog(stage).toPath();
    }

    private void loadPic(Path path) throws IOException{
        file = Files.readAllBytes(path);
    }

    public static void main(String[] args) {
        launch();
    }
}
