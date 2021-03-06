package dev.mtvjr.websocket_demo.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public final class TextMessage extends MessageBase {
    private String message;
    private String author;

    @Override
    public String getMessageType() {
        return "TextMessage";
    }
}
