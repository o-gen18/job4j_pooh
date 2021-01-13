package jms;

import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {
    private final Map<String, Queue<String>> messages = new ConcurrentHashMap<>();

    @Override
    public void get(Connection connection, String nameOfQueue) {
        java.util.Queue<String> queue = messages.get(nameOfQueue);
        if (queue == null) {
            connection.write("Such queue doesn't exist");
        } else {
            String response = queue.isEmpty() ? "The queue is empty" : queue.poll();
            connection.write(response);
        }
    }

    @Override
    public void post(Connection connection, String nameOfQueue, String text) {
        java.util.Queue<String> tmpQueue = messages.putIfAbsent(
                nameOfQueue, new ConcurrentLinkedQueue<>(Collections.singleton(text)));
        if (tmpQueue != null) {
            tmpQueue.add(text);
        }
    }
}
