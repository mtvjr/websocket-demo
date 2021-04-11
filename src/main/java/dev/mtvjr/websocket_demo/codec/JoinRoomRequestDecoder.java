package dev.mtvjr.websocket_demo.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mtvjr.websocket_demo.messages.JoinRoomRequest;
import org.atmosphere.config.managed.Decoder;

import java.io.IOException;

public class JoinRoomRequestDecoder implements Decoder<String, JoinRoomRequest> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public JoinRoomRequest decode(String s) {
        try {
            return this.mapper.readValue(s, JoinRoomRequest.class);
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }
}
