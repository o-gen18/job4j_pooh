package jms;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private AtomicInteger subscriberId = new AtomicInteger(0);
    private int port;
    private ServerSocket serverSocket;
    private Connection connection;
    private Service topicService = new TopicService();
    private Service queueService = new QueueService();

    private ExecutorService pool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    public Service getQueueService() {
        return queueService;
    }

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!serverSocket.isClosed()) {
                Connection connection = new Connection(serverSocket);
                pool.execute(
                        new ConnectionHandler(connection, subscriberId.incrementAndGet(), topicService, queueService));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Server(8000).start();
    }
}
