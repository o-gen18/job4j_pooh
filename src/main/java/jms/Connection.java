package jms;

/*
This object encapsulates socket and IO Streams for either server or client.
Both server and client delegate to this object writing and reading operations.
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection implements AutoCloseable {
    private final Socket socket;

    private final BufferedReader reader;

    private final BufferedWriter writer;

    private BufferedReader createReader() throws IOException {
        return new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()));
    }

    private BufferedWriter createWriter() throws IOException {
        return new BufferedWriter(
                new OutputStreamWriter(
                        socket.getOutputStream()));
    }

    /*
    Creates this object for a client.
     */
    public Connection(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.reader = createReader();
            this.writer = createWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    Creates this object for a server.
     */
    public Connection(ServerSocket serverSocket) {
        try {
            this.socket = serverSocket.accept();
            this.reader = createReader();
            this.writer = createWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    Writes a String into the output stream of an opened socket.
     */
    public void write(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    Reads a line from the input stream of an opened socket.
     */
    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean ready() {
        try {
            return reader.ready();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isClosed() {
        return this.socket.isClosed();
    }

    @Override
    public void close() throws Exception {
        reader.close();
        writer.close();
        socket.close();
    }
}
