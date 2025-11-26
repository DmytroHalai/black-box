package org.example.web.utils;

public enum Message {
    DONE("That's right! Well done!"),
    WRONG("Try again!!");

    private final String text;

    Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}