package jms;

public interface Service {
     /*
    Responds to the client's "GET" request.
    Sends them a message
     */

    public void get(Connection connection, String nameOfMode);

    /*
    Saves the message posted by a client,
    and makes it ready to be read.
     */

    public void post(Connection connection, String nameOfMode, String text);
}
