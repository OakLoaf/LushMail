package org.lushplugins.lushmail.mail;

import org.bukkit.entity.Player;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.lushmail.LushMail;

public class ItemMail extends Mail {

    public ItemMail(String id, String sender) {
        super(id, "item", sender, "Gift from " + sender);
    }

    public ItemMail(String id, String sender, String title) {
        super(id, "item", sender, title);
    }

    @Override
    public void open(Player player) {
        if (false) { // TODO: Check if state is "opened"
            ChatColorHandler.sendMessage(player, LushMail.getInstance().getConfigManager().getMessage("already-opened-mail", "&cThis mail has already been opened!"));
        }
    }

    @Override
    public void preview(Player player) {

    }

    @Override
    public DisplayItemStack getPreviewItem() {
        return null;
    }
}
