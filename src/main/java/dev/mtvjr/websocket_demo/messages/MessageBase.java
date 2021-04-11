package dev.mtvjr.websocket_demo.messages;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public abstract class MessageBase {
    private UUID uuid = UUID.randomUUID();
    private long time = new Date().getTime();
    private UUID sender;

    public abstract String getMessageType();
}
