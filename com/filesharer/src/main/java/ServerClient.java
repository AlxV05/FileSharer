package main.java;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Objects;

public class ServerClient {
    protected Socket clientSocket;
    private BufferedReader in;
    private Writer out;
    String temporaryIPAddress = "127.0.0.1";
    int temporaryPort = 1234;

    public void startConnection(String ip, int port) throws IOException {
        this.clientSocket = new Socket(ip, port);
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendCommand(String cmd) {
        return this.handleCmd(cmd);
    }

    public void killConnection() {
        try {
            this.out.close();
            this.in.close();
            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleCmd(String cmd) {
        if (this.clientSocket == null || this.clientSocket.isClosed()) {
            if (Objects.equals(cmd, "connect")) {
                try {
                    this.startConnection(temporaryIPAddress, temporaryPort);
                    return "Connected";
                } catch (IOException e) {
                    return "Failed to connect";
                }
            } else {
                return "No connection";
            }
        } else {
            try {
                switch (cmd) {
                    case "kill" -> {
                        this.out.write(cmd);
                        this.killConnection();
                        return "Connection killed";
                    }
                    case "connect" -> {
                        if (this.clientSocket == null || this.clientSocket.isClosed()) {
                            try {
                                this.startConnection(temporaryIPAddress, temporaryPort);
                                return "Connected";
                            } catch (ConnectException e) {
                                return "Failed to connect";
                            }
                        } else {
                            return "Already connected";
                        }
                    }
                    case "list" -> {
                        this.out.write(cmd);
                        return this.in.readLine();
                    }
                    case "read" -> {
                        return null;
                    }
                    case "push" -> {
                        return null;
                    }
                    case "pull" -> {
                        return null;
                    }
                    default -> {
                        return String.format("Unknown command: %s", cmd);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
