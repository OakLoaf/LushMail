package org.lushplugins.lushmail.mail;

import org.bukkit.entity.Player;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.lushmail.LushMail;

import java.util.ArrayList;
import java.util.List;

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
     * @return The prepared preview item to be shown in guis
     */
    public DisplayItemStack getPreparedPreviewItem() {
        DisplayItemStack.Builder previewItemBuilder = DisplayItemStack.Builder.of(this.getPreviewItem());

        List<String> lore = previewItemBuilder.getLore() != null ? new ArrayList<>(previewItemBuilder.getLore()) : new ArrayList<>();
        DisplayItemStack previewLayout = LushMail.getInstance().getConfigManager().getGuiItem("preview-mail");
        if (previewLayout != null) {
            List<String> previewLore = previewLayout.getLore();
            if (previewLore != null) {
                lore.addAll(previewLore);
                lore.replaceAll((line) -> line
                    .replace("%mail_id%", this.getId())
                    .replace("%sender%", this.getSender()));
            }

            previewItemBuilder.setLore(lore);
        }

        return previewItemBuilder.build();
    }

    /**
     * To get the prepared preview item see {@link Mail#getPreparedPreviewItem()}
     * @return The mail's preview item
     */
    public abstract DisplayItemStack getPreviewItem();

    public static class State {
        public static final String OPENED = "opened";
        public static final String UNOPENED = "unopened";
    }
}
