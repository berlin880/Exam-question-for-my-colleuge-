/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package text.field.server;



/**
 *
 * @author abd elrahman
 */
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client extends Application {
    private TextArea receivedTextArea;
    private TextArea sendTextArea;
    private OutputStream outputStream;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        receivedTextArea = new TextArea();
        receivedTextArea.setEditable(false);
        sendTextArea = new TextArea();
        sendTextArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String message = sendTextArea.getText();
                sendTextArea.clear();
                sendMessage("Abelrahman Adel _  21100797: " + message);
            }
        });

        root.getChildren().addAll(receivedTextArea, sendTextArea);

        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.setTitle("[Your_First_Name]_[Your_Registration_Number]");
        primaryStage.show();

        connectToServer();
    }

    private void connectToServer() {
        try {
             Socket clientSocket = new Socket("127.0.0.1", 10000);
            InputStream inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();
            Thread receiveThread = new Thread(() -> receiveMessages(inputStream));
            receiveThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessages(InputStream inputStream) {
        byte[] buffer = new byte[1024];
        int bytesRead;

        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                String message = new String(buffer, 0, bytesRead);
                Platform.runLater(() -> receivedTextArea.appendText(message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        try {
            // Send the message to the server
            outputStream.write(message.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
