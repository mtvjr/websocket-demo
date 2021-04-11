package dev.mtvjr.websocket_demo.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class JoinRoomRequest extends MessageBase {
    private String username;

    @Override
    public String getMessageType() {
        return "JoinRoomRequest";
    }
}
