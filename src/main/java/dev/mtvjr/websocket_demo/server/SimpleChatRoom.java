package dev.mtvjr.websocket_demo.server;

import dev.mtvjr.websocket_demo.codec.MessageEncoder;
import dev.mtvjr.websocket_demo.codec.TestMessageDecoder;
import dev.mtvjr.websocket_demo.messages.*;
import org.atmosphere.config.service.*;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

// Commented out to prevent from running
// @ManagedService(path="/chat")
public class SimpleChatRoom {
    private final Logger logger = LoggerFactory.getLogger(SimpleChatRoom.class);

    @Ready
    @DeliverTo(DeliverTo.DELIVER_TO.BROADCASTER)
    public String onReady(final AtmosphereResource r) {
        logger.info("Browser {} connected", r.uuid());
        return "A new user has joined!";
    }

    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        if (event.isCancelled()) {
            logger.info("Browser {} unexpectedly disconnected", event.getResource().uuid());
        } else if (event.isClosedByClient()) {
            logger.info("Browser {} closed the connection", event.getResource().uuid());
        }
    }

    @Message(encoders = MessageEncoder.class, decoders = TestMessageDecoder.class)
    public TextMessage onTextMessage(TextMessage textMessage) throws IOException {
        return textMessage;
    }
}
