package dev.mtvjr.websocket_demo.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class MessageResponse extends MessageBase {
    private String info;
    private UUID originalMessage;
    private ResponseCode code;
    private String messageType;

    public MessageResponse(MessageBase msg, ResponseCode code) {
        this.originalMessage = msg.getUuid();
        this.code = code;
        this.messageType = msg.getMessageType() + "Response";
    }

    public MessageResponse(MessageBase msg, ResponseCode code, String info) {
        this(msg, code);
        this.info = info;
    }

}
