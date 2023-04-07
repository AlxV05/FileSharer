package main.java;

import java.io.*;
import java.net.Socket;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.Objects;

import static main.java.Messages.CLIInput.ArgumentPlaceholders;
import static main.java.Messages.CLIInput.Commands;
import static main.java.Messages.CLIOutput.*;

public class ServerClient {
    protected Socket clientSocket;
    private BufferedReader serverInputReader;
    private PrintWriter serverOutputWriter;

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
        return handleLine(line);
    }


    private String handleLine(String line) {
        String[] splitLine = line.split(" ");
        String cmd = splitLine[0];
        if (clientSocket == null || clientSocket.isClosed()) {
            if (Objects.equals(cmd, Commands.startConnection)) {
                try {
                    startConnection("127.0.01", 1234);
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
                    case Commands.killConnection -> {
                        serverOutputWriter.write(cmd);
                        killConnection();
                        return Successes.connectionKillSuccess;
                    }
                    case Commands.readFile -> {
                        try {
                            String argTag = splitLine[1];
                            serverOutputWriter.println(String.format(ArgumentPlaceholders.doubleArgs, cmd, argTag));
                            return String.format(serverInputReader.readLine()); // halp
                        } catch (IndexOutOfBoundsException e) {
                            return Failures.unspecifiedFileToRead;
                        }
                    }
                    case Commands.pushFile -> {
                        try {
                            String argTag = splitLine[1];
                            String argPath = splitLine[2];
                            File file = new File(argPath);
                            byte[] fileBytes = new byte[(int) file.length()];
                            try (FileInputStream fileReader = new FileInputStream(file)) {
                                if (fileReader.read(fileBytes) != fileBytes.length) {
                                    return Failures.byteReadLengthFailure;
                                }
                            }
                            StringBuilder byteString = new StringBuilder();
                            for (byte b : fileBytes) {
                                byteString.append(b).append(",");
                            }
                            serverOutputWriter.println(String.format(ArgumentPlaceholders.tripleArgs, Commands.pushFile, argTag, byteString));
                            return serverInputReader.readLine();
                        } catch (NoSuchFileException | FileNotFoundException e) {
                            return String.format(Failures.noFileAtPath, splitLine[2]);
                        } catch (IndexOutOfBoundsException e) {
                            return Failures.argumentRequirementFailure;
                        }
                    }
                    case Commands.pullFile -> {
                        try {
                            String argTag = splitLine[1];
                            String argPath = splitLine[2];
                            serverOutputWriter.println(String.format(ArgumentPlaceholders.doubleArgs, cmd, argTag));
                            String fileData = serverInputReader.readLine();
                            String[] x = fileData.split(",");
                            byte[] y = new byte[x.length];
                            for (int i = 0; i < x.length; i++) {
                                y[i] = Byte.parseByte(x[i]);
                            }
                            String z = new String(y);
                            File file = new File(argPath);
                            if (!file.createNewFile()) {
                                if (file.exists()) {
                                    return String.format(Failures.fileAlreadyExistsAtPath, argPath);
                                } else {
                                    return String.format(Failures.failedToCreateNewFileAtPath, argPath);
                                }
                            }
                            FileWriter clientFileWriter = new FileWriter(file);
                            clientFileWriter.write(fileData);
                            clientFileWriter.close();
                            return String.format(Successes.pulledFileSuccessfully, argTag, argPath);
                        } catch (IOException e) {
                            return Failures.writeToFileFailed;
                        } catch (IndexOutOfBoundsException e) {
                            return Failures.argumentRequirementFailure;
                        }
                    }
                    case Commands.removeFile -> {
                        try {
                            String argTag = splitLine[1];
                            serverOutputWriter.println(String.format(ArgumentPlaceholders.doubleArgs, cmd, argTag));
                            return serverInputReader.readLine();
                        } catch (IndexOutOfBoundsException e) {
                            return Failures.unspecifiedFileToRemove;
                        }
                    }
                    case Commands.help -> {
                        return Helps.fullHelp;
                    }
                    default -> {
                        serverOutputWriter.println(line);
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
                if (Objects.equals(line, Commands.exitClient)) {
                    killConnection();
                    break;
                } else {
                    System.out.println(sendCommand(line));
                }
            } catch (IOException e) {
                System.out.println(Failures.lineReadFailure);
            }
        }
        System.out.println(Statuses.closingClient);
    }

    public static void main(String[] args) {
        ServerClient client = new ServerClient();
        if (args.length > 0) {
            try {
                client.startConnection(args[0], Integer.parseInt(args[1]));
            } catch (Exception e) {
                System.out.println(Failures.serverSpecifiedPortError);
            }
        }
        client.mainLoop();
    }
}
