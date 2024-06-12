package org.lushplugins.lushmail.mail;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushlib.gui.inventory.SimpleGui;
import org.lushplugins.lushlib.utils.SimpleItemStack;

public abstract class Mail {
    private final String id;
    private final String type;
    private final String sender;
    private final String title;

    public Mail(String id, String type, String sender) {
        this.id = id;
        this.type = type;
        this.sender = sender;
        this.title = null;
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
     * Open this mail for a player
     * @param player The player opening the mail
     */
    public abstract void open(Player player);

    /**
     * @return The preview item to be shown in the mail gui
     */
    public abstract SimpleItemStack getPreviewItem();

    /**
     * @return The preview
     */
    public @Nullable SimpleGui getPreviewGui() {
        return null;
    }

    public static class State {
        public static final String OPENED = "opened";
        public static final String UNOPENED = "unopened";
    }
}
