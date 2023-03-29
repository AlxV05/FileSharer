package main.java;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.function.DoubleToIntFunction;

public class ServerThread extends Thread{
    protected ServerDataHandler dataHandler;
    protected Socket socket;

    public ServerThread(ServerDataHandler dataHandler, Socket socket) {
        this.socket = socket;
        this.dataHandler = dataHandler;
    }

    public void run() {
        BufferedReader in;
        PrintStream out;
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintStream(this.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        while (!this.socket.isClosed()) {
            try {
//                out.println(this.handleLine(in.readLine()));
                out.println(in.readLine()); // Issue here
            } catch (Exception e) {
                e.printStackTrace();
                return;
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
        if (Objects.equals(line, null)) {
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
//                return this.dataHandler.listFiles();
                return "Yes";
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
                return String.format("Unknown command {%s} should not have reached here", cmd);
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
