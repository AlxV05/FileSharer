package main.java;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class ServerThread extends Thread{
    protected ServerDataHandler dataHandler;
    protected Socket clientSocket;
    protected ServerSocket serverSocket;

    public ServerThread(ServerSocket serverSocket, ServerDataHandler dataHandler, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.dataHandler = dataHandler;
        this.serverSocket = serverSocket;
    }

    public void run() {
        BufferedReader clientInputReader;
        PrintWriter clientOutputWriter;
        try {
            clientInputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            clientOutputWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        while (!clientSocket.isClosed()) {
            try {
                clientOutputWriter.println(handleLine(clientInputReader.readLine()));
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        try {
            clientInputReader.close();
            clientOutputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleLine(String line) {
        if (Objects.equals(line, null) || line.isBlank()) {
            return "";
        } else {
            String[] splitLine = line.split(" ", 3);
            return parseLine(splitLine);
        }
    }

    private String parseLine(String[] splitLine) {
        String cmd = splitLine[0];
        switch (cmd) {
            case "kill" -> {
                killConnection();
                return null;
            }
            case "connect" -> {
                return "Already connected";
            }
            case "list" -> {
                return dataHandler.listFiles();
            }
            case "read", "pull" -> {
                String argTag = splitLine[1];
                return dataHandler.readFile(argTag);
            }
            case "push" -> {
                String argTag = splitLine[1];
                String argInfo = splitLine[2];
                dataHandler.addFile(new FileDataObject(argTag, argInfo));
                return String.format("Added file with tag \"%s\" to database", argTag);
            }
            case "remove" -> {
                String argTag = splitLine[1];
                dataHandler.removeFile(argTag);
                return String.format("Removed file with tag \"%s\"", argTag);
            }
            default -> {
                return String.format("Unknown command: \"%s\"", cmd);
            }
        }
    }

    public void killConnection() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
