package test.java;

import main.java.ServerClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

public class TestClients {
    @Test
    public void testClient1() {
        ServerClient client1 = new ServerClient();
        try {
            client1.startConnection("127.0.0.1", 1234);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(client1.sendCommand("Hello"));
        assert Objects.equals(client1.sendCommand("Hello"), "Unknown command: Hello");
        client1.killConnection();
    }

    @Test
    public void testClient2() {
        ServerClient client2 = new ServerClient();
        try {
            client2.startConnection("127.0.0.1", 1234);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(client2.sendCommand("Bob joe"));
        assert Objects.equals(client2.sendCommand("Bob joe"), "Unknown command: Bob");
        client2.killConnection();
    }
}
