package com.internship.codes;

import java.io.*;
import java.net.*;

public class chatclient {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            Thread readThread = new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    System.out.println("❌ Disconnected from server.");
                }
            });
            readThread.start();

            String userInput;
            while ((userInput = consoleInput.readLine()) != null) {
                if (userInput.equalsIgnoreCase("/exit")) {
                    System.out.println("👋 You have left the chat.");
                    out.close();
                    socket.close();
                    break;
                } else {
                    out.println(userInput);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}
