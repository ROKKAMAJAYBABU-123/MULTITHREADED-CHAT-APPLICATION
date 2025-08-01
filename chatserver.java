package com.internship.codes;

import java.io.*;
import java.net.*;
import java.util.*;

public class chatserver {

    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("💬 Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("✅ New client connected: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.out.println("❌ Server error: " + e.getMessage());
        }
    }

    public static void broadcast(String message, ClientHandler sender) {
        synchronized (clientHandlers) {
            for (ClientHandler client : clientHandlers) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }
}

class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String name;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Enter your name:");
            name = in.readLine();
            System.out.println("👤 " + name + " joined the chat.");
            chatserver.broadcast("👋 " + name + " has joined the chat.", this);

            String message;
            while ((message = in.readLine()) != null) {
                String formattedMessage = name + ": " + message;
                System.out.println(formattedMessage);
                chatserver.broadcast(formattedMessage, this);
            }
        } catch (IOException e) {
            System.out.println("❌ " + name + " disconnected.");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {}
            chatserver.removeClient(this);
            chatserver.broadcast("👋 " + name + " has left the chat.", this);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
