package org.lushplugins.lushmail.data;

import org.bukkit.entity.Player;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.mail.Mail;
import org.lushplugins.lushmail.storage.StorageManager;

import java.util.UUID;

public class ReceivedMail {
    private final UUID receiver;
    private final String id;
    private String state;
    private boolean favourited;

    public ReceivedMail(UUID receiver, String id, String state, boolean favourited) {
        this.receiver = receiver;
        this.id = id;
        this.state = state;
        this.favourited = favourited;
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

    public void open() {
        if (state.equals(Mail.State.OPENED)) {
            return;
        }

        LushMail.getInstance().getStorageManager().setMailState(receiver, id, Mail.State.OPENED).thenAccept(ignored -> {
            // TODO: Implement open mail logic
        });
    }
}
