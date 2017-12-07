package main.java.org.elsys.api.services;

public class Message {

    private String name;
    private String message;

    public Message(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getMessage() {
        return String.format("\"%s\",\"%s\"", name, message);
    }

}
