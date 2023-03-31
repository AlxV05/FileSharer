package main.java;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerHandler {
    protected ServerSocket serverSocket;
    protected ServerDataHandler dataHandler;

    public ServerHandler() {
        this.dataHandler = new ServerDataHandler();
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (!serverSocket.isClosed()) {
            try {
                new ServerThread(dataHandler, serverSocket.accept()).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    public static void main(String[] args) {
        int temporaryPort = 1234;
        ServerHandler server = new ServerHandler();
        System.out.println("Starting server");
        server.start(temporaryPort);
    }
}
