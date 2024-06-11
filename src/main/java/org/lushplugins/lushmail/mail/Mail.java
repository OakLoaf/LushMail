package org.lushplugins.lushmail.mail;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushlib.gui.inventory.SimpleGui;
import org.lushplugins.lushlib.utils.SimpleItemStack;

public abstract class Mail {
    private final String id;
    private final String type;

    public Mail(String id, String type) {
        this.id = id;
        this.type = type;
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
