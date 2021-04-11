package dev.mtvjr.websocket_demo.server;

import dev.mtvjr.websocket_demo.messages.TextMessage;
import org.slf4j.Logger;

import java.util.*;

public class RoomState {
    Map<UUID, ChatroomUser> users = new HashMap<>();
    Set<UUID> messages = new HashSet<>();

    /**
     * Checks if a user is already registered
     * @param uuid - The UUID of a user
     * @param username - The username of a user
     * @return Returns true if the sender or username have been registered
     */
    boolean doesUserExist(UUID uuid, String username) {
        for (ChatroomUser user : users.values()) {
            if (user.getUuid().equals(uuid)) return true;
            if (user.getUsername().equals(username)) return true;
        }
        return false;
    }

    /**
     * Retrieve the username for a user.
     * @param user - The UUID of the user
     * @return The username if the user is found, otherwise null
     */
    String getUsername(UUID user) {
        return users.get(user).getUsername();
    }

    /**
     * Register a user
     * @param user - User to register.
     */
    void addUser(ChatroomUser user) {
        users.put(user.getUuid(), user);
    }

    /**
     * Remove a user via its UUID
     * @param user - The UUID of the user
     */
    public void removeUser(UUID user) {
        users.remove(user);
    }

    /**
     * Store a text message
     * @param message - The message to store
     */
    public void addTextMessage(TextMessage message) {
        messages.add(message.getUuid());
    }

    /**
     * Checks to see if a text message exists in the database.
     * @param uuid - The UUID of the text message.
     * @return True if the message exists, false otherwise
     */
    public boolean doesTextMessageExist(UUID uuid) {
        return messages.contains(uuid);
    }

    /**
     * Removes a text message via its UUID
     * @param uuid - The UUID of the text message to remove
     */
    public void removeTextMessage(UUID uuid) {
        messages.remove(uuid);
    }

    /**
     * Dumps the room state to the log.
     * @param logger - A logger to use to print the information.
     */
    public void dump(Logger logger) {
        StringBuilder builder = new StringBuilder();
        builder.append("Users: ").append(users.size());
        users.forEach((uuid, user) -> {
            builder.append('\n')
                    .append(uuid.toString())
                    .append(": ")
                    .append(user.getUsername());
            if (user.isModerator()) {
                builder.append(" (Moderator)");
            }
        });
        builder.append("Messages").append(messages.size());
        messages.forEach(uuid -> {
            builder.append('\n').append(uuid);
        });
        logger.info(builder.toString());
    }
}
