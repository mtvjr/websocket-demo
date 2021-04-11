package dev.mtvjr.websocket_demo.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
public class JoinRoomResponse extends MessageResponse {
    Boolean isModerator;

    public JoinRoomResponse(MessageBase msg, ResponseCode code) {
        super(msg, code);
    }

    public JoinRoomResponse(MessageBase msg, ResponseCode code, String info) {
        super(msg, code, info);
    }
}
