package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.Objects;

import static main.java.Messages.CLIOutput.*;
import static main.java.Messages.CLIInput.*;

public class ServerHandler {
    protected ServerSocket serverSocket;
    protected ServerDataHandler dataHandler;
    protected SudoClient sudoClient;
    protected Thread acceptingThread;

    public void start(int port) {
        System.out.println(Statuses.startingServer);
        try {
            this.serverSocket = new ServerSocket(port);
            this.dataHandler = new ServerDataHandler();
            this.sudoClient = new SudoClient();
            this.acceptingThread = new Thread(this::startAcceptingLoop);
            acceptingThread.start();
            sudoClient.startConnection("127.0.0.1", port);
            sudoClient.mainLoop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.serverSocket.close();
            this.acceptingThread.interrupt();
            this.dataHandler = null;
            this.sudoClient = null;
        } catch (Exception ignore) {
        }
        System.out.println(Statuses.closingServer);
    }

    public void startAcceptingLoop() {
        while (!serverSocket.isClosed()) {
            try {
                new ServerThread(serverSocket, dataHandler, serverSocket.accept()).start();
            } catch (IOException ignored) {
                // Temporary
            }
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    public static void main(String[] args) {
        int serverPort;
        if (args.length > 0) {
            try {
                serverPort = Integer.parseInt(args[1]);
            }
            catch (Exception e) {
                System.out.println(Failures.serverSpecifiedPortError);
                serverPort = 1234;
            }
        } else {
            serverPort = 1234;
        }
        ServerHandler server = new ServerHandler();
        server.start(serverPort);
    }

    private final class SudoClient extends ServerClient {
        @Override
        public void killConnection() {
            try {
                super.killConnection();
                stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void mainLoop() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(Successes.sudoClientStartSuccess);
            for (;;) {
                System.out.print(prompt);
                try {
                    String line = reader.readLine();
                    if (Objects.equals(line, Commands.exitClient) || Objects.equals(line, Commands.killConnection)) {
                        sendCommand(Commands.killConnection);
                        killConnection();
                        break;
                    } else {
                        System.out.printf(sendCommand(line));
                    }
                } catch (IOException e) {
                    System.out.println(Failures.lineReadFailure);
                }
            }
            System.out.println(Statuses.closingSudoClient);
        }
    }
}
