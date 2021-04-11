package dev.mtvjr.websocket_demo.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class RoomStateTest {
    @Test
    void testDoesUserExist() {
        RoomState state = new RoomState();
        UUID uuid = UUID.randomUUID();
        state.addUser(uuid, "Michael");

        // Should return true if the UUID or if the username exists
        Assertions.assertTrue(state.doesUserExist(uuid, "Steven"));
        Assertions.assertTrue(state.doesUserExist(UUID.randomUUID(), "Michael"));

        // Otherwise, should be false
        Assertions.assertFalse(state.doesUserExist(UUID.randomUUID(), "Steven"));
    }

    @Test
    void testAddUser() {
        RoomState state = new RoomState();
        UUID uuid = UUID.randomUUID();
        state.addUser(uuid, "Michael");
        Assertions.assertEquals("Michael", state.getUsername(uuid));
    }
}