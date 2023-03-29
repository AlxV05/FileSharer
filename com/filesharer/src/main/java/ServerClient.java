package main.java;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Objects;

public class ServerClient {
    protected Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    String temporaryIPAddress = "127.0.0.1";
    int temporaryPort = 1234;

    public void startConnection(String ip, int port) throws IOException {
        this.clientSocket = new Socket(ip, port);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public String sendCommand(String line) {
        return this.handleLine(line) + "\n";
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

    private String handleLine(String line) {
        String[] splitLine = line.split(" ");
        String cmd = splitLine[0];
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
                    case "read" -> {
                        try {
                            String arg = splitLine[1];
                            this.out.println(String.format("%s %s", cmd, arg));
                            return this.in.readLine();
                        } catch (IndexOutOfBoundsException e) {
                            return "No file specified to read";
                        }
                    }
                    case "push" -> {
                        return null;
                    }
                    case "pull" -> {
                        return null;
                    }
                    case "help" -> {
                        return null;
                    }
                    default -> {
                        this.out.println(cmd);
                        return this.in.readLine();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
