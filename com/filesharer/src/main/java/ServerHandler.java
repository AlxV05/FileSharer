package main.java;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerHandler {
    protected ServerSocket serverSocket;

    public void start(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (!this.serverSocket.isClosed()) {
            try {
                new ServerThread(this.serverSocket.accept()).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() throws IOException {
        this.serverSocket.close();
    }

    public static void main(String[] args) {
        int temporaryPort = 1234;
        ServerHandler server = new ServerHandler();
        server.start(temporaryPort);
    }
}
