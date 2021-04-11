package dev.mtvjr.websocket_demo.server;

import dev.mtvjr.websocket_demo.codec.JoinRoomMessageDecoder;
import dev.mtvjr.websocket_demo.codec.TestMessageDecoder;
import dev.mtvjr.websocket_demo.codec.MessageEncoder;
import dev.mtvjr.websocket_demo.messages.*;
import org.atmosphere.config.managed.Encoder;
import org.atmosphere.config.service.*;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceFactory;
import org.atmosphere.cpr.BroadcasterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.UUID;

@ManagedService(path="/chat")
public class ChatRoom {
    /// Enables logging
    private final Logger logger = LoggerFactory.getLogger(ChatRoom.class);

    /// Tracks the state of the room
    private final RoomState state = new RoomState();

    /// Encodes messages for custom send functions
    /// Not required for @Message handling
    private final Encoder<MessageBase, String> encoder = new MessageEncoder();

    @Inject
    BroadcasterFactory broadcasterFactory;

    @Inject
    AtmosphereResourceFactory resourceFactory;

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

        // Either way, remove the user
        state.removeUser(UUID.fromString(event.getResource().uuid()));
    }

    // Messages default to sending to everyone in the broadcast
    // Therefore, we need to specify that only the recipient gets the message
    @DeliverTo(DeliverTo.DELIVER_TO.RESOURCE)
    @Message(encoders = MessageEncoder.class, decoders = JoinRoomMessageDecoder.class)
    public MessageResponse onJoinRequest(JoinRoom request) {
        String user = request.getUsername() + " (" + request.getSender() + ')';
        logger.info("{} attempted to register.", user);
        if (state.doesUserExist(request.getSender(), request.getUsername())) {
            logger.warn("{} attempted to register twice.", user);
            JoinRoomResponse response = new JoinRoomResponse(request, ResponseCode.DENIED, "User already exists");
            response.setIsModerator(false);
            return response;
        }
        logger.info("{} successfully registered.", user);
        boolean isModerator = request.getUsername().equals("Moderator");
        state.addUser(new ChatroomUser(request.getSender(), request.getUsername(), isModerator));
        JoinRoomResponse response = new JoinRoomResponse(request, ResponseCode.OK);
        response.setIsModerator(isModerator);
        return response;
    }

    // Messages default to sending to everyone in the broadcast
    // Therefore, @DeliverTo(DeliverTo.DELIVER_TO.BROADCAST) is not needed
    @Message(encoders = MessageEncoder.class, decoders = TestMessageDecoder.class)
    public TextMessage onTextMessage(TextMessage textMessage) throws IOException {
        String author = state.getUsername(textMessage.getSender());
        if (author == null) {
            logger.warn("Unable to find user with UUID {}", textMessage.getSender());
            state.dump(logger);
            // Unable to find user, send NACK back to sender
            MessageResponse response = new MessageResponse(textMessage, ResponseCode.FORBIDDEN, "You are not registered");
            if (!sendToUser(textMessage.getSender(), response)) {
                logger.warn("Unable to send TextMessageResponse to {}", textMessage.getSender());
            }
            return null;
        }
        textMessage.setAuthor(author);
        state.addTextMessage(textMessage);
        return textMessage;
    }

    /**
     * Send a message to a given user
     * @param uuid - The sender ID of the user to receive the message.
     * @param message - The message for that user to receive
     * @return True if the message was sent, false otherwise
     */
    public boolean sendToUser(UUID uuid, MessageBase message) {
        AtmosphereResource resource = resourceFactory.find(uuid.toString());
        if (resource == null) {
            logger.warn("Unable to find resource for user {}", uuid);
            return false;
        }
        String json = encoder.encode(message);
        broadcasterFactory.lookup("/chat").broadcast(json, resource);
        return true;
    }
}
