package jms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class ServerImpl{
    private java.util.Queue<String> topicQueue = new ConcurrentLinkedQueue<>();
    private java.util.Queue<String> queueQueue = new ConcurrentLinkedQueue<>();

    private ExecutorService pool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    public void start(int port) {
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                Socket socket = server.accept();
                try (OutputStream out = socket.getOutputStream();
                     BufferedReader in = new BufferedReader(
                             new InputStreamReader(socket.getInputStream()))) {
                    pool.submit(() -> {
                        StringBuilder message = new StringBuilder();
                        String str = null;
                        try {
                            str = in.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        while (!str.isEmpty()) {
                            message.append(str);
                        }
                        String[] array = str.split(" ");
                    });
                    String str = in.readLine();
                    while (!str.isEmpty()) {
                        System.out.println(str);
                        str = in.readLine();
                    }
                    out.write("HTTP/1.1 200 OK\r\n".getBytes());
                    out.write(Thread.currentThread().getName().getBytes());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {

    }
}
