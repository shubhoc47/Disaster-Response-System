/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs.server;

/**
 *
 * @author shubh
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DRSServer {
    public static final int PORT = 8888;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("SERVER: DRS Server is running and listening on port " + PORT);

            // This loop runs forever, waiting for clients.
            while (true) {
                // The accept() method blocks (waits) until a client connects.
                Socket clientSocket = serverSocket.accept();
                System.out.println("SERVER: New client connected from " + clientSocket.getInetAddress().getHostAddress());

                // Create a new handler for this client and start it on a new thread.
                // This allows the server to handle multiple clients at once.
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            System.err.println("SERVER: Exception in server socket: " + e.getMessage());
            e.printStackTrace();
        }
    }
}