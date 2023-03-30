package test.java;

import main.java.FileDataReader;
import main.java.ServerClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInteractionTest {
    public static void main(String[] args) throws IOException {
//        ServerClient client = new ServerClient();
//        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//        System.out.println("Started client");
//        System.out.print("> ");
//        while (true) {
//            System.out.printf(client.sendCommand(in.readLine()));
//            System.out.print("> ");
//        }
        ProcessBuilder processBuilder = new ProcessBuilder("ls");
        processBuilder.directory(new File("/Users/AlexVO5/"));
        processBuilder.inheritIO();
        processBuilder.start();
    }
}
