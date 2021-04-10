package dev.mtvjr.websocket_demo.server;

import dev.mtvjr.websocket_demo.codec.TextMessageCodec;
import dev.mtvjr.websocket_demo.messages.TextMessage;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@ManagedService(path="/chat")
public class ChatRoom {
    private final Logger logger = LoggerFactory.getLogger(ChatRoom.class);

    @Ready
    public void onReady(final AtmosphereResource r) {
        logger.info("Browser {} connected", r.uuid());
    }

    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        if (event.isCancelled()) {
            logger.info("Browser {} unexpectedly disconnected", event.getResource().uuid());
        } else if (event.isClosedByClient()) {
            logger.info("Browser {} closed the connection", event.getResource().uuid());
        }
    }

    @Message(encoders = TextMessageCodec.class, decoders = TextMessageCodec.class)
    public TextMessage onMessage(TextMessage textMessage) throws IOException {
        logger.info("{} just sent {}", textMessage.getAuthor(), textMessage.getMessage());
        return textMessage;
    }
}
