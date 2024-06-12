package org.lushplugins.lushmail.data;

import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.mail.Mail;

import java.util.UUID;

public class ReceivedMail {
    private final String id;
    private final UUID receiver;
    private String state;
    private boolean favourited;
    private final long timeSent;
    private final long timeout;

    public ReceivedMail(String id, UUID receiver, String state, boolean favourited, long timeSent, long timeout) {
        this.id = id;
        this.receiver = receiver;
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

    public void open() {
        if (state.equals(Mail.State.OPENED)) {
            return;
        }

        LushMail.getInstance().getStorageManager().setMailState(receiver, id, Mail.State.OPENED).thenAccept(ignored -> {
            // TODO: Implement open mail logic
        });
    }
}
