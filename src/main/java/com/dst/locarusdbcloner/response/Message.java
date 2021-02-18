package com.dst.locarusdbcloner.response;

public class Message {

    private LocarusResultField result;
    private String description;
    private int code;

    public LocarusResultField getResult() {
        return result;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Message{" +
                "data=" + result +
                '}';
    }
}
