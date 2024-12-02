package de.uulm.in.vs.grn.p3a;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class SockagramUI extends Application{

    private Path imagePath;
    private byte[] file;
    private SockagramClient sock;
    private final int sceneWith = 550;
    private final int sceneHight = 650;
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
        Scene scene = new Scene(hbox,sceneWith,sceneHight);
        stage.setScene(scene);
        stage.show();
    }
    private void setSendScene(Stage stage){
        HBox hbox = new HBox();
        VBox vBox = new VBox(hbox);
        Label l = new Label("Choose your Image: ");
        Button selectButton = new Button("Select Image");
        Button sendButton = new Button("Send Image to Server");

        ComboBox<String> comboBox = new ComboBox<String>();
        comboBox.getItems().addAll("NO Filter","Black White","Eightbit","YOLO","SWAG(dreht den Swag auf jo)","Summer","Sepia");



        //idk what im doing if im being honest this uses way to many global variables to be good code ngl
        EventHandler<ActionEvent> selectButtonEvent =  new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                getImage(stage);
                try {
                    loadPic(imagePath);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                l.setText("Selected Image: " + imagePath.getFileName() + "  ");
                selectButton.setText("Change Image");
                //man hätte gleich mit dem InputStream arbeiten können aber hab das schon alles mit byte[] gemacht ich schreib das jetzt nicht um
                InputStream inputStream = new ByteArrayInputStream(file);
                ImageView imageView = new ImageView(new Image(inputStream));
                imageView.setFitWidth(500);//since all the pictures from the server are 500x500 we can just make it the same here
                imageView.setFitHeight(500);
                vBox.getChildren().add(imageView);
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
        Scene scene = new Scene(vBox, sceneWith,sceneHight);
        stage.setScene(scene);
        stage.show();

    }
    private void setImageRecivedScene(Stage stage) throws IOException{
        VBox vbox = new VBox();
        Label l = new Label("Image Received :)");
        file = sock.getImageResponse();
        ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(file)));
        Button saveImage = new Button("Would you like to save this Image?");
        Button closeWindow = new Button("Close window and Connection");

        EventHandler<ActionEvent> saveImageEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                writeImage(stage);
            }
        };
        EventHandler<ActionEvent> closeWindowEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    sock.closeConnection();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.exit();
            }
        };
        saveImage.setOnAction(saveImageEvent);
        closeWindow.setOnAction(closeWindowEvent);
        System.out.println("Image received");
        imageView.setFitHeight(500);
        imageView.setFitWidth(500);
        vbox.getChildren().addAll(l,imageView,saveImage,closeWindow);
        vbox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vbox,sceneWith,sceneHight);
        stage.setScene(scene);
        stage.show();
    }


    private void writeImage(Stage stage){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        try {
            StringBuilder fileName = new StringBuilder(imagePath.getFileName().toString());
            fileName.insert(fileName.indexOf("."),"_1");//fügt nur ein _1 am Ende des Bild namens hinzu
            Path path = Paths.get(directoryChooser.showDialog(stage).toPath().toString(),fileName.toString());
            Files.write(path,file);// why no work
            System.out.println("saved image here:" + path.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void getImage(Stage stage){
        FileChooser fileChooser = new FileChooser();
        this.imagePath =  fileChooser.showOpenDialog(stage).toPath();
        String[] fileEndings = this.imagePath.toString().split("\\.");//ist einfach der path nach . aufgeteilt
        //ist bisschen overkill aber das verwirklicht das tatsächliche file system verhaltend as eigentlich nur die letzte endung letzte z.B. image.png.txt ist ne text datei bei den meisten Betriebssystemen
        String fileEnding = fileEndings[fileEndings.length-1] ;
        //schaut das es ein Bild ist und repromted das man ein andere datei auswählen kann
        if(!Objects.equals(fileEnding, "png") && !Objects.equals(fileEnding,"jpg") && !Objects.equals(fileEnding,"jpeg")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error not a Picture");
            alert.setHeaderText("Please select a Picture with either png,jpg or jpeg file endings");
            alert.showAndWait();
            getImage(stage);
        }
    }

    private void loadPic(Path path) throws IOException{
        file = Files.readAllBytes(path);
    }

    //NICHT DIREKT AUSFÜHREN SONDER ./gradlew run
    public static void main(String[] args) {
        launch();
    }
}
