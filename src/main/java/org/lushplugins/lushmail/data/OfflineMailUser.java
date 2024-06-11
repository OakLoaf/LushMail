package org.lushplugins.lushmail.data;

import java.util.UUID;

public class OfflineMailUser {
    private final UUID uuid;
    private final String username;

    public OfflineMailUser(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }
}
