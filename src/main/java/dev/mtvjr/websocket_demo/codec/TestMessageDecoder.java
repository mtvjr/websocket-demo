package dev.mtvjr.websocket_demo.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mtvjr.websocket_demo.messages.TextMessage;
import org.atmosphere.config.managed.Decoder;
import org.atmosphere.config.managed.Encoder;

import java.io.IOException;

public class TestMessageDecoder implements Decoder<String, TextMessage> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public TextMessage decode(String s) {
        try {
            return this.mapper.readValue(s, TextMessage.class);
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }
}
