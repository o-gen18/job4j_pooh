package jms;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.Arrays;

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
            System.out.println(Thread.currentThread().getName() + " starts parsing input....");
            String request = subscriber.readLine(); //reads the first line with http request
            StringBuilder message = new StringBuilder();
            while (subscriber.ready()) { //reads remaining json-format message
                message.append(subscriber.readLine());
                System.out.println(Thread.currentThread().getName() + "inside loop for json");
            }
            System.out.println(Thread.currentThread().getName() + "is reading from client");
            String json = message.toString();
            System.out.println(Thread.currentThread().getName() + " has json: json");
            JsonObject jsonObject = json.isEmpty()? null : Json.createReader(new StringReader(json)).readObject();
            String[] words = request.split("\\s/|/");
            System.out.println(Arrays.toString(words));
            switch (words[0]) {
                case ("GET"):
                    switch (words[1]) {
                        case ("queue"):
                            queueService.get(subscriber, words[2]);
                            System.out.println(Thread.currentThread().getName() + " just read from queue");
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
                            System.out.println(Thread.currentThread().getName() + " just posted in queue");
                            break;
                        case ("topic"):


                            break;
                    }
                    break;
            }
        }
    }
}
