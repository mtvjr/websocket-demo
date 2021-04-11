package dev.mtvjr.websocket_demo.server;

import dev.mtvjr.websocket_demo.messages.TextMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

class RoomStateTest {
    RoomState state;
    UUID uuid;

    @BeforeEach
    void beforeEach() {
        state = new RoomState();
        uuid = UUID.randomUUID();
    }

    @Test
    void testDoesUserExist() {
        ChatroomUser user = new ChatroomUser(uuid, "Michael", false);
        state.addUser(user);

        // Should return true if the UUID or if the username exists
        Assertions.assertTrue(state.doesUserExist(uuid, "Steven"));
        Assertions.assertTrue(state.doesUserExist(UUID.randomUUID(), "Michael"));

        // Otherwise, should be false
        Assertions.assertFalse(state.doesUserExist(UUID.randomUUID(), "Steven"));

        state.removeUser(uuid);
        Assertions.assertFalse(state.doesUserExist(UUID.randomUUID(), "Michael"));
        Assertions.assertFalse(state.doesUserExist(uuid, "Steven"));
    }

    @Test
    void testGetUsername() {
        ChatroomUser user = new ChatroomUser(uuid, "Michael", false);
        state.addUser(user);
        Assertions.assertEquals("Michael", state.getUsername(uuid));
    }

    @Test
    void testTextMessageRecollection() {
        TextMessage msg = new TextMessage();
        msg.setAuthor("Michale");
        msg.setMessage("Hello World!");
        msg.setSender(uuid);

        Assertions.assertFalse(state.doesTextMessageExist(msg.getUuid()));
        state.addTextMessage(msg);
        Assertions.assertTrue(state.doesTextMessageExist(msg.getUuid()));
        state.removeTextMessage(msg.getUuid());
        Assertions.assertFalse(state.doesTextMessageExist(msg.getUuid()));
    }
}