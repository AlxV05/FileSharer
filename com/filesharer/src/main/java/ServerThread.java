package main.java;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

import static main.java.Messages.CLIInput.*;
import static main.java.Messages.CLIOutput.*;

public class ServerThread extends Thread{
    protected ServerDataHandler dataHandler;
    protected Socket clientSocket;
    protected ServerSocket serverSocket;

    public ServerThread(ServerSocket serverSocket, ServerDataHandler dataHandler, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.dataHandler = dataHandler;
        this.serverSocket = serverSocket;
    }

    @Override
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
            case Commands.killConnection -> {
                killConnection();
                return Successes.connectionKillSuccess;
            }
            case Commands.startConnection -> {
                return Statuses.alreadyConnected;
            }
            case Commands.listFiles -> {
                return dataHandler.listFiles();
            }
            case Commands.readFile, Commands.pullFile -> {
                String argTag = splitLine[1];
                return dataHandler.readFile(argTag);
            }
            case Commands.pushLoop -> {
                String argTag = splitLine[1];
                String argInfo = splitLine[2];
                if (dataHandler.containsFile(argTag)) {
                    dataHandler.appendToFile(argTag, argInfo);
                } else {
                    dataHandler.addFile(new FileDataObject(argTag, List.of(argInfo)));
                }
                return null;
            }
            case Commands.pushComplete -> {
                String argTag = splitLine[1];
                return String.format(Successes.addedFileSuccessfully, argTag);
            }
            case Commands.removeFile -> {
                String argTag = splitLine[1];
                dataHandler.removeFile(argTag);
                return String.format(Successes.removedFileSuccessfully, argTag);
            }
            default -> {
                return String.format(Failures.unknownCommand, cmd);
            }
        }
    }

    private void killConnection() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
