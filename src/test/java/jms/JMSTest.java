package jms;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JMSTest {
    private Server server;
    private final String ip = "localhost";
    private final int port = 9000;

    @Before
    public void startServer() {
        new Thread(() -> {
            server = new Server(port);
            server.start();
        }).start();
    }

    @After
    public void close() {
        server.close();
    }

    @Test
    public void whenWriteToQueueThenReadInTurns() throws InterruptedException {
        Thread.sleep(3000);
        Client publisher = new Client();
        Client reader1 = new Client();
        Client reader2 = new Client();
        Client reader3 = new Client();
        String[] postQueue = {"POST /queue\n"+
                "{\n" +
                "  \"queue\" : \"weather\",\n" +
                "  \"text\" : \"temperature +11 C\"\n" +
                "}", "POST /queue\n"+
                "{\n" +
                "  \"queue\" : \"weather\",\n" +
                "  \"text\" : \"temperature +22 C\"\n" +
                "}", "POST /queue\n"+
                "{\n" +
                "  \"queue\" : \"weather\",\n" +
                "  \"text\" : \"temperature +33 C\"\n" +
                "}"};
        publisher.connect(ip, port);
        for (String post : postQueue) {
            publisher.request(post);
        }
        reader1.connect(ip, port);
        reader2.connect(ip, port);
        reader3.connect(ip, port);
        reader1.request("GET /queue/weather");
        reader2.request("GET /queue/weather");
        reader3.request("GET /queue/weather");
        String result1 = reader1.readResponse();
        String result2 = reader2.readResponse();
        String result3 = reader3.readResponse();
        reader1.disconnect();
        reader2.disconnect();
        reader3.disconnect();
        assertThat(result1, is("POST /queue\n"+
                "{\n" +
                "  \"queue\" : \"weather\",\n" +
                "  \"text\" : \"temperature +11 C\"\n" +
                "}"));
        assertThat(result2, is("POST /queue\n"+
                "{\n" +
                "  \"queue\" : \"weather\",\n" +
                "  \"text\" : \"temperature +22 C\"\n" +
                "}"));
        assertThat(result3, is("POST /queue\n"+
                "{\n" +
                "  \"queue\" : \"weather\",\n" +
                "  \"text\" : \"temperature +33 C\"\n" +
                "}"));
    }

    @Test
    public void when1PublishesTopicAnd3ReadItThenTheyAllGetIt() {
        Client publisher = new Client();
        List<Client> clients = List.of(new Client(), new Client(), new Client());
        String getTopic = "GET /topic/weather\n{\n" +
                "  \"topic\" : \"weather\",\n" +
                "  \"text\" : \"temperature +18 C\"\n" +
                "}";

        String topic = "{\n" +
                "  \"topic\" : \"weather\",\n" +
                "  \"text\" : \"temperature +18 C\"\n" +
                "}";

        java.util.Queue<String> results = new LinkedList<>();

        clients.forEach(client -> {
            new Thread(() -> {
                client.connect(ip, port);
                client.request(getTopic);
            }).start();
        });

        String post = "POST /topic/weather\n{\n" +
                "  \"queue\" : \"weather\",\n" +
                "  \"text\" : \"temperature +18 C\"\n" +
                "}" ;

        publisher.connect(ip, port);
        publisher.request(post);

        clients.forEach(client -> {
            results.add(client.readResponse());
            client.disconnect();
        });
        server.close();
        assertThat(results.poll(), is(topic));
        assertThat(results.poll(), is(topic));
        assertThat(results.poll(), is(topic));
    }
}
