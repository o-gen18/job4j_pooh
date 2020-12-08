package jms;

import java.io.IOException;

public class Client {
    private Connection connection;

    public void connect(String ip, int port) {
        this.connection = new Connection(ip, port);
    }

    public void disconnect() {
        try {
            this.connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void request(String request) {
        if (connection == null || connection.isClosed()) {
            System.out.println("There is no connection to the server");
        }
        connection.write(request);
    }

    public String readResponse() {
        StringBuilder result = new StringBuilder();
        while (connection.ready()) {
            result.append(connection.readLine());
        }
        return result.toString();
    }
}
