package dev.mtvjr.websocket_demo.server;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.slf4j.Logger;

import java.util.UUID;

public class RoomState {
    private final BidiMap<UUID, String> users = new DualHashBidiMap<>();

    /**
     * Checks if a user is already registered
     * @param sender - The sender UUID of a user
     * @param username - The username of a user
     * @return Returns true if the sender or username have been registered
     */
    boolean doesUserExist(UUID sender, String username) {
        String savedUsername = users.get(sender);
        UUID savedUUID  = users.getKey(username);
        return savedUsername != null || savedUUID != null;
    }

    String getUsername(UUID sender) {
        return users.get(sender);
    }

    void addUser(UUID sender, String username) {
        users.put(sender, username);
    }

    public void dump(Logger logger) {
        StringBuilder builder = new StringBuilder();
        builder.append("Users: ").append(users.size());
        users.forEach((uuid, name) -> {
            builder.append('\n')
                .append(uuid.toString())
                .append(": ")
                .append(name);
        });
        logger.info(builder.toString());
    }
}
