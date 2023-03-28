package main.java;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class ServerClient {
    protected Socket clientSocket;
    private BufferedReader in;
    private Writer out;
    String temporaryIPAddress = "127.0.0.1";

    public void startConnection(String ip, int port) {
        try {
            this.clientSocket = new Socket(ip, port);
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to start the connection");
        }
    }

    public String sendCommand(String cmd) {
        if (this.clientSocket == null || this.clientSocket.isClosed()) {
            if (Objects.equals(cmd, "connect")) {
                this.startConnection(temporaryIPAddress, 1234);
                return "Connected";
            } else {
                return "No connection";
            }
        } else {
            if (cmd.equals("kill")) {
                try {
                    this.out.write(cmd);
                    this.killConnection();
                    return "Connection killed";
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Failed to kill the connection";
                }
            } else {
                try {
                    this.out.write(cmd);
                    return in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    return "An error occurred while sending a command";
                }
            }
        }
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
}
