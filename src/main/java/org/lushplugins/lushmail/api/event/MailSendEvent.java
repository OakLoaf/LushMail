package org.lushplugins.lushmail.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushmail.mail.Mail;

public class MailSendEvent extends MailEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final @Nullable Player sender;
    private final @Nullable Player receiver;

    public MailSendEvent(@NotNull Mail mail, @Nullable Player sender, @Nullable Player receiver) {
        super(mail);
        this.sender = sender;
        this.receiver = receiver;
    }

    public @Nullable Player getSender() {
        return sender;
    }

    public @Nullable Player getReceiver() {
        return receiver;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
