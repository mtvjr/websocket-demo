package dev.mtvjr.websocket_demo.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Represents additional user data not contained in the resource
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatroomUser {
    /**
     * A unique identifier for a user. This should be equal to the resource UUID.
     */
    private UUID uuid;

    /**
     * A unique user facing username for the user.
     */
    private String username;

    /**
     * A flag to indicate if the user is able to perform moderator actions.
     */
    private boolean isModerator = false;
}
