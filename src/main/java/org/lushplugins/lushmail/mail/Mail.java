package org.lushplugins.lushmail.mail;

import org.bukkit.entity.Player;
import org.lushplugins.lushlib.utils.DisplayItemStack;

public abstract class Mail {
    private final String id;
    private final String type;
    private final String sender;
    private final String title;

    public Mail(String id, String type, String sender) {
        this(id, type, sender, null);
    }

    public Mail(String id, String type, String sender, String title) {
        this.id = id;
        this.type = type;
        this.sender = sender;
        this.title = title;
    }

    /**
     * @return The unique id of this mail
     */
    public String getId() {
        return id;
    }

    /**
     * @return The mail type
     */
    public String getType() {
        return type;
    }

    /**
     * @return The sender of the message
     */
    public String getSender() {
        return sender;
    }

    /**
     * @return The title of this mail
     */
    public String getTitle() {
        return title;
    }

    /**
     * Open this mail for a player
     * @param player The player opening the mail
     */
    public abstract void open(Player player);

    /**
     * Preview the mail for a player
     * @param player The player previewing the mail
     */
    public abstract void preview(Player player);

    /**
     * @return The preview item to be shown in the mail gui
     */
    public abstract DisplayItemStack getPreviewItem();

    public static class State {
        public static final String OPENED = "opened";
        public static final String UNOPENED = "unopened";
    }
}
