package org.lushplugins.lushmail.data;

import org.lushplugins.lushmail.mail.Mail;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MailUser extends OfflineMailUser {
    private final List<UUID> ignoredUsers;
    private final HashMap<String, String> receivedMailStates;

    public MailUser(UUID uuid, String username, List<UUID> ignoredUsers, HashMap<String, String> receivedMailStates) {
        super(uuid, username);
        this.ignoredUsers = ignoredUsers;
        this.receivedMailStates = receivedMailStates;
    }

    public boolean isIgnoringUser(UUID uuid) {
        return ignoredUsers.contains(uuid);
    }

    public void addIgnoredUser(UUID uuid) {
        ignoredUsers.add(uuid);
    }

    public void removeIgnoredUser(UUID uuid) {
        ignoredUsers.add(uuid);
    }

    public boolean hasReceived(String mailId) {
        return receivedMailStates.containsKey(mailId);
    }

    public boolean hasOpened(String mailId) {
        return receivedMailStates.containsKey(mailId) && receivedMailStates.get(mailId).equals(Mail.State.OPENED);
    }
}
