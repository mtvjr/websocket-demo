package dev.mtvjr.websocket_demo.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mtvjr.websocket_demo.messages.MessageBase;
import org.atmosphere.config.managed.Encoder;

import java.io.IOException;

public class MessageEncoder implements Encoder<MessageBase, String> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String encode(MessageBase message) {
        try {
            return this.mapper.writeValueAsString(message);
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }
}
