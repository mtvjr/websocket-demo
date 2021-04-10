package dev.mtvjr.websocket_demo.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mtvjr.websocket_demo.messages.TextMessage;
import org.atmosphere.config.managed.Decoder;
import org.atmosphere.config.managed.Encoder;

import java.io.IOException;

public class TextMessageCodec implements Encoder<TextMessage,String>, Decoder<String, TextMessage> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public TextMessage decode(String s) {
        try {
            return this.mapper.readValue(s, TextMessage.class);
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    @Override
    public String encode(TextMessage textMessage) {
        try {
            return this.mapper.writeValueAsString(textMessage);
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }
}
