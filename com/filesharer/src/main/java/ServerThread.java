package main.java;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class ServerThread extends Thread{
    protected ServerDataHandler dataHandler;
    protected Socket socket;

    public ServerThread(ServerDataHandler dataHandler, Socket socket) {
        this.socket = socket;
        this.dataHandler = dataHandler;
    }

    public void run() {
        BufferedReader in;
        PrintWriter out;
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        while (!this.socket.isClosed()) {
            try {
                out.println(this.handleLine(in.readLine()));
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleLine(String line) {
        if (Objects.equals(line, null) || line.isBlank()) {
            return "";
        } else {
            String[] splitLine = line.split(" ");
            return parseLine(splitLine);
        }
    }

    private String parseLine(String[] splitLine) {
        String cmd = splitLine[0];
        switch (cmd) {
            case "kill" -> {
                this.killConnection();
                return null;
            }
            case "list" -> {
                return this.dataHandler.listFiles();
            }
            case "read" -> {
                String arg = splitLine[1];
                return this.dataHandler.readFile(arg);
            }
            case "push" -> {
                return null;
            }
            case "pull" -> {
                return null;
            }
            default -> {
                return String.format("Unknown command: \"%s\"", cmd);
            }
        }
    }

    public void killConnection() {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
