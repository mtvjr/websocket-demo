package dev.mtvjr.websocket_demo.messages;

import java.util.Date;

@lombok.Data
public final class TextMessage {
    private String message;
    private String author;
    private long time;

    public TextMessage() {
        this("", "");
    }

    public TextMessage(String author, String message) {
        this.author = author;
        this.message = message;
        this.time = new Date().getTime();
    }
}
