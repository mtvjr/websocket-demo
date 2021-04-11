package dev.mtvjr.websocket_demo.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mtvjr.websocket_demo.messages.JoinRoom;
import org.atmosphere.config.managed.Decoder;

import java.io.IOException;

public class JoinRoomMessageDecoder implements Decoder<String, JoinRoom> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public JoinRoom decode(String s) {
        try {
            return this.mapper.readValue(s, JoinRoom.class);
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }
}
