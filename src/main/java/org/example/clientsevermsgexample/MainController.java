package org.example.clientsevermsgexample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.*;
import java.net.Socket;

public class MainController {

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public void initialize() {
        try {
            System.out.println("Trying to connect to server...");
            socket = new Socket("localhost", 1234); // Must match Server.java
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connected to server!");

            // Thread to continuously receive messages
            Thread receiveThread = new Thread(() -> {
                try {
                    while (true) {
                        String message = input.readUTF();
                        Platform.runLater(() -> chatArea.appendText("Server: " + message + "\n"));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> chatArea.appendText("Disconnected from server.\n"));
                    e.printStackTrace();
                }
            });
            receiveThread.setDaemon(true);
            receiveThread.start();
        } catch (IOException e) {
            chatArea.appendText("Error connecting to server: " + e.getMessage() + "\n");
            e.printStackTrace();
        }

        // Handle send button click or Enter key
        sendButton.setOnAction(e -> sendMessage());
        messageField.setOnAction(e -> sendMessage());
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            try {
                output.writeUTF(message);
                chatArea.appendText("Client: " + message + "\n");
                messageField.clear();
            } catch (IOException e) {
                chatArea.appendText("Error sending message.\n");
                e.printStackTrace();
            }
        }
    }
}
