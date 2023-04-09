package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

import static main.java.Messages.CLIInput.Commands;
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
        if (Objects.equals(line, null) || line.isEmpty() || line.isBlank()) {
            return "";
        }
        String[] splitLine = line.split(" ", 3);
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
            case Commands.pushFile -> {
                String argTag = splitLine[1];
                String argInfo = splitLine[2];
                dataHandler.addFile(argTag, argInfo);
                return String.format(Successes.pushedFileSuccessfully, argTag);
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
