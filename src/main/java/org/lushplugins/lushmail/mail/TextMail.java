package org.lushplugins.lushmail.mail;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushlib.utils.SimpleItemStack;
import org.lushplugins.lushmail.util.StringUtils;

public class TextMail extends Mail {
    private String text;

    public TextMail(String id, String text) {
        super(id, "text");
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void open(Player player) {
        ChatColorHandler.sendMessage(player, text);
    }

    @Override
    public SimpleItemStack getPreviewItem() {
        SimpleItemStack item = new SimpleItemStack(Material.PAPER);
        item.setDisplayName("Message");
        item.setLore(StringUtils.splitByCount(text, 50));

        return item;
    }
}
