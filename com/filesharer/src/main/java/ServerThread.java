package main.java;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class ServerThread extends Thread{
    protected Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        BufferedReader in;
        PrintStream out;
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintStream(this.socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        while (!this.socket.isClosed()) {
            try {
                out.println(handleLine(in.readLine()));
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        try {
            in.close();
            out.close();
        } catch (Exception e) {
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
        String command = splitLine[0];
        switch (command) {
            case "kill" -> {
                try {
                    this.socket.close();
                    return "Connection killed";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Failed to kill the connection";
                }
            }
            case "connect" -> {
                return "Already connected";
            }
            default -> {
                return String.format("Unknown command: %s", command);
            }
        }
    }
}
