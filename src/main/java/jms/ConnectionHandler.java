package jms;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;

public class ConnectionHandler implements Runnable {
    private Service topicService;
    private Service queueService;

    private Connection subscriber;
    private int subscriberId;

    public ConnectionHandler(Connection subscriber, int id, Service topicService, Service queueService) {
        this.subscriber = subscriber;
        this.subscriberId = id;
        this.queueService = queueService;
        this.topicService = topicService;
    }

    @Override
    public void run() {
        while (!subscriber.isClosed()) {
            String request = subscriber.readLine(); //reads the first line with http request
            StringBuilder message = new StringBuilder();
            while (subscriber.ready()) { //reads remaining json-format message
                message.append(subscriber.readLine());
            }
            System.out.println(Thread.currentThread().getName() + "is reading from client");
            String json = message.toString();
            JsonObject jsonObject = Json.createReader(new StringReader(json)).readObject();
            String[] words = request.split("^\\s|/");
            switch (words[0]) {
                case ("GET"):
                    switch (words[1]) {
                        case ("queue"):
                            queueService.get(subscriber, words[2]);
                            break;
                        case ("topic"):

                            break;
                    }
                    break;
                case ("POST"):
                    switch (words[1]) {
                        case ("queue"):
                            String nameOfQueue = jsonObject.getString("queue");
                            String text = jsonObject.getString("text");
                            queueService.post(subscriber, nameOfQueue, text);
                            break;
                        case ("topic"):


                            break;
                    }
                    break;
            }
        }
    }
}
