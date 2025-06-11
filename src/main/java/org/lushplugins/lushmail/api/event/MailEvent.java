package org.lushplugins.lushmail.api.event;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushmail.mail.Mail;

public abstract class MailEvent extends Event {
    protected Mail mail;

    public MailEvent(@NotNull Mail mail) {
        this.mail = mail;
    }

    @NotNull
    public final Mail getMail() {
        return this.mail;
    }
}
