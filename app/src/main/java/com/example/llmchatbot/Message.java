package com.example.llmchatbot;

public class Message {

    private String messageText;
    private String sender;
    private String time;

    public Message(String messageText, String sender, String time) {
        this.messageText = messageText;
        this.sender = sender;
        this.time = time;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getSender() {
        return sender;
    }

    public String getTime() {
        return time;
    }
}