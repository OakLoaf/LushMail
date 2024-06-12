package org.lushplugins.lushmail.mail;

import org.bukkit.entity.Player;
import org.lushplugins.lushlib.utils.SimpleItemStack;

public class CommandMail extends Mail {

    public CommandMail(String id, String sender) {
        super(id, "command", sender, "Gift from " + sender);
    }

    public CommandMail(String id, String sender, String title) {
        super(id, "command", sender, title);
    }

    @Override
    public void open(Player player) {

    }

    @Override
    public SimpleItemStack getPreviewItem() {
        return null;
    }
}
