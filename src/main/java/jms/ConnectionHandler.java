package jms;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionHandler implements Runnable {

    private Map<Integer, Queue<String>> subscribersTopics = new ConcurrentHashMap<>();
    private Map<String, java.util.Queue<String>> topicMap = new ConcurrentHashMap<>();
    private Map<String, java.util.Queue<String>> queueMap = new ConcurrentHashMap<>();
    private Set<String> topicsNamesSet = ConcurrentHashMap.newKeySet();

    private Connection subscriber;
    private int subscriberId;

    public ConnectionHandler(Connection subscriber, int id) {
        this.subscriber = subscriber;
        this.subscriberId = id;
    }

    @Override
    public void run() {
        while (!subscriber.isClosed()) {
            String request = subscriber.readLine(); //reads the first line with http request
            StringBuilder message = new StringBuilder();
            while (subscriber.ready()) { //reads remaining json-format message
                message.append(subscriber.readLine());
            }
            String json = message.toString();
            JsonObject jsonObject = Json.createReader(new StringReader(json)).readObject();

            String[] words = request.split("^\\s|/");
            switch (words[0]) {
                case ("GET"):
                    switch (words[1]) {
                        case ("queue"):
                            if (queueMap.containsKey(words[2])) {
                                java.util.Queue<String> queue = queueMap.get(words[2]);
                                String response = queue.isEmpty() ? "The queue is empty" : queue.poll();
                                subscriber.write(response);
                            } else {
                                subscriber.write("Such queue doesn't exist");
                            }
                            break;
                        case ("topic"):
                            if (topicsNamesSet.contains(words[2])) {
                                if (subscribersTopics.containsKey(subscriberId)) {
                                    java.util.Queue<String> personalTopic = subscribersTopics.get(subscriberId);
                                    String response =
                                            personalTopic.isEmpty()? "Topic is empty" : personalTopic.poll();
                                    subscriber.write(response);
                                } else {
                                    java.util.Queue<String> personalTopic =
                                            new ConcurrentLinkedQueue<String>();
                                    subscribersTopics.put(subscriberId, personalTopic);
                                    subscriber.write("You've just subscribed to the topic - " + words[2]);
                                }
                            } else {
                                subscriber.write("Such topic doesn't exist");
                            }
                            break;
                    }
                    break;
                case ("POST"):
                    switch (words[1]) {
                        case ("queue"):
                            String nameOfQueue = jsonObject.getString("queue");
                            String text = jsonObject.getString("text");
                            if (queueMap.containsKey(nameOfQueue)) {
                                queueMap.get(nameOfQueue).add(text);
                            } else {
                                java.util.Queue<String> newQueue = new ConcurrentLinkedQueue<String>();
                                newQueue.add(text);
                                queueMap.put(nameOfQueue, newQueue);
                            }
                            break;
                        case ("topic"):
                            String nameOfTopic = jsonObject.getString("topic");
                            String text2 = jsonObject.getString("text");
                            topicsNamesSet.add(nameOfTopic);
                            subscribersTopics.forEach((
                                    id, personalTopic) -> personalTopic.add(text2));
                            break;
                    }
                    break;
            }
        }
    }
}
