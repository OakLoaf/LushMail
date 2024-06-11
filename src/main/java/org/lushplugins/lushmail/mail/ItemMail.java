package org.lushplugins.lushmail.mail;

import org.bukkit.entity.Player;
import org.lushplugins.lushlib.utils.SimpleItemStack;

public class ItemMail extends Mail {

    public ItemMail(String id) {
        super(id, "item");
    }

    @Override
    public void open(Player player) {

    }

    @Override
    public SimpleItemStack getPreviewItem() {
        return null;
    }
}
