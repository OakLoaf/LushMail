package org.lushplugins.lushmail.mail;

import org.bukkit.entity.Player;
import org.lushplugins.lushlib.utils.SimpleItemStack;

public class CommandMail extends Mail {

    public CommandMail(String id) {
        super(id, "command");
    }

    @Override
    public void open(Player player) {

    }

    @Override
    public SimpleItemStack getPreviewItem() {
        return null;
    }
}
