package org.lushplugins.lushmail.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.mail.Mail;
import org.lushplugins.lushmail.storage.StorageManager;

import java.util.UUID;

public class ReceivedMail {
    private final String id;
    private final UUID receiverUuid;
    private String state;
    private boolean favourited;
    private final long timeSent;
    private final long timeout;

    public ReceivedMail(String id, UUID receiverUuid, String state, boolean favourited, long timeSent, long timeout) {
        this.id = id;
        this.receiverUuid = receiverUuid;
        this.state = state;
        this.favourited = favourited;
        this.timeSent = timeSent;
        this.timeout = timeout;
    }

    public String getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isFavourited() {
        return favourited;
    }

    public void setFavourited(boolean favourited) {
        this.favourited = favourited;
    }

    public long getTimeSent() {
        return timeSent;
    }

    public long getTimeout() {
        return timeout;
    }

    public void preview() {
        Player receiver = Bukkit.getPlayer(receiverUuid);
        if (receiver == null) {
            return;
        }

        LushMail.getInstance().getStorageManager().loadMail(id).thenAccept(mail -> {
            mail.preview(receiver);
        });

    }

    public void open() {
        Player receiver = Bukkit.getPlayer(receiverUuid);
        if (receiver == null) {
            return;
        }

        StorageManager storageManager = LushMail.getInstance().getStorageManager();
        storageManager.loadMail(id).thenAccept(mail -> {
            if (!state.equals(Mail.State.OPENED)) {
                storageManager.setMailState(receiverUuid, id, Mail.State.OPENED).thenAccept((ignored) -> {
                    mail.open(receiver);
                });
            } else {
                mail.open(receiver);
            }
        });
    }
}
