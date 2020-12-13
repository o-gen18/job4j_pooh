package jms;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class TopicService implements Service {
    private Map<String, Queue<String>> topicMap = new ConcurrentHashMap<>();

    @Override
    public void get(Connection connection, String nameOfTopic) {

    }

    @Override
    public void post(Connection connection, String nameOfTopic, String text) {

    }
}
