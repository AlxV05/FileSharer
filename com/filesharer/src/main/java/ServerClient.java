package main.java;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Objects;

import static main.java.Messages.CLIOutput.*;

public class ServerClient {
    protected Socket clientSocket;
    private BufferedReader serverInputReader;
    private PrintWriter serverOutputWriter;
    String temporaryIPAddress = "127.0.0.1";
    int temporaryPort = 1234;

    public ServerClient() {
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        serverInputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        serverOutputWriter = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public void killConnection() {
        try {
            serverOutputWriter.close();
            serverInputReader.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException ignore) {
        }
    }

    public String sendCommand(String line) {
        return handleLine(line) + "\n";
    }


    private String handleLine(String line) {
        String[] splitLine = line.split(" ");
        String cmd = splitLine[0];
        if (clientSocket == null || clientSocket.isClosed()) {
            if (Objects.equals(cmd, "connect")) {
                try {
                    startConnection(temporaryIPAddress, temporaryPort);
                    return Successes.connectionSuccess;
                } catch (IOException e) {
                    return Failures.connectionFailure;
                }
            } else {
                return Failures.noConnection;
            }
        } else {
            try {
                switch (cmd) {
                    case "kill" -> {
                        serverOutputWriter.write(cmd);
                        killConnection();
                        return Successes.connectionKillSuccess;
                    }
                    case "read" -> {
                        try {
                            String argTag = splitLine[1];
                            serverOutputWriter.println(String.format("%s %s", cmd, argTag));
                            return serverInputReader.readLine();
                        } catch (IndexOutOfBoundsException e) {
                            return "No file specified to read";
                        }
                    }
                    case "push" -> {
                        try {
                            String argTag = splitLine[1];
                            String argPath = splitLine[2];
                            String fileData = String.join("%n", Files.readAllLines(new File(argPath).toPath()));
                            serverOutputWriter.print(String.format("%s %s ", cmd, argTag));
                            serverOutputWriter.println(fileData);
                            return serverInputReader.readLine();
                        } catch (NoSuchFileException e) {
                            return String.format("No file with path \"%s\" found", splitLine[2]);
                        } catch (IndexOutOfBoundsException e) {
                            return "Argument requirements not met";
                        }
                    }
                    case "pull" -> {
                        try {
                            String argTag = splitLine[1];
                            String argPath = splitLine[2];
                            serverOutputWriter.println(String.format("%s %s", cmd, argTag));
                            String fileData = serverInputReader.readLine();
                            File file = new File(argPath);
                            if (!file.createNewFile()) {
                                return "Failed to create file OR file already exists";
                            }
                            FileWriter clientFileWriter = new FileWriter(file);
                            clientFileWriter.write(String.format(fileData + "%n"));
                            clientFileWriter.close();
                            return String.format("Successfully pulled file \"%s\" from server to \"%s\"", argTag, argPath);
                        } catch (IOException e) {
                            return "Failed to write file";
                        } catch (IndexOutOfBoundsException e) {
                            return "Argument requirements not met";
                        }
                    }
                    case "remove" -> {
                        try {
                            String argTag = splitLine[1];
                            serverOutputWriter.println(String.format("%s %s", cmd, argTag));
                            return serverInputReader.readLine();
                        } catch (IndexOutOfBoundsException e) {
                            return "No file specified to remove";
                        }
                    }
                    case "help" -> {
                        return Helps.fullHelp;
                    }
                    default -> {
                        serverOutputWriter.println(cmd);
                        return serverInputReader.readLine();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void mainLoop() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(Successes.clientStartSuccess);
        for (;;) {
            System.out.print(prompt);
            try {
                String line = reader.readLine();
                if (Objects.equals(line, "exit")) {
                    killConnection();
                    break;
                } else {
                    System.out.printf(sendCommand(line));
                }
            } catch (IOException e) {
                System.out.println(Failures.lineReadFailure);
            }
        }
        System.out.println(Statuses.closingClient);
    }

    public static void main(String[] args) throws IOException {
        ServerClient client = new ServerClient();
        if (args.length > 0) {
            client.startConnection(args[0], Integer.parseInt(args[1]));
        }
        client.mainLoop();
    }
}
