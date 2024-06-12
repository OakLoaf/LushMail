package org.lushplugins.lushmail.mail;

import org.bukkit.entity.Player;
import org.lushplugins.lushlib.utils.SimpleItemStack;

public class ItemMail extends Mail {

    public ItemMail(String id, String sender) {
        super(id, "item", sender, "Gift from " + sender);
    }

    public ItemMail(String id, String sender, String title) {
        super(id, "item", sender, title);
    }

    @Override
    public void open(Player player) {

    }

    @Override
    public SimpleItemStack getPreviewItem() {
        return null;
    }
}
