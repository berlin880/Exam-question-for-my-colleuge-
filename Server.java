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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Application {
    private TextArea receivedTextArea;
    private TextArea sendTextArea;
    private List<OutputStream> outputStreams;

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
                sendMessageToClients("[AAST]: " + message);
            }
        });

        root.getChildren().addAll(receivedTextArea, sendTextArea);

        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.setTitle("AAST");
        primaryStage.show();

        outputStreams = new ArrayList<>();
        startServer();
    }

    private void startServer() {
        new Thread(() -> {
            try{
                ServerSocket serverSocket = new ServerSocket(10000);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    OutputStream outputStream = clientSocket.getOutputStream();
                    outputStreams.add(outputStream);
                    Thread clientThread = new Thread(() -> handleClient(clientSocket));
                    clientThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleClient(Socket clientSocket) {
        try {
            InputStream inputStream = clientSocket.getInputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                String message = new String(buffer, 0, bytesRead);
                Platform.runLater(() -> receivedTextArea.appendText(message));
                sendMessageToClients(message);
            }
            inputStream.close();
            clientSocket.close();
            outputStreams.remove(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToClients(String message) {
        byte[] buffer = message.getBytes();
        for (OutputStream outputStream : outputStreams) {
            try {
                outputStream.write(buffer);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}

