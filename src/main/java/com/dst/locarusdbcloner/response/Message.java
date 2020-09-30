package com.dst.locarusdbcloner.response;

public class Message {

    private LocarusResultField result;
    private String description;

    public LocarusResultField getResult() {
        return result;
    }

    public void setResult(LocarusResultField result) {
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Message{" +
                "data=" + result +
                '}';
    }
}
