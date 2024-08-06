package org.lushplugins.lushmail.mail;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.util.StringUtils;

import java.util.List;

public class TextMail extends Mail {
    private String text;

    public TextMail(String id, String text, String sender) {
        super(id, "text", sender, "Letter from " + sender);
        this.text = text;
    }

    public TextMail(String id, String text, String sender, String title) {
        super(id, "text", sender, title);
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
        preview(player);
    }

    @Override
    public void preview(Player player) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        if (book.getItemMeta() instanceof BookMeta bookMeta) {
            List<String> pages = StringUtils.splitByCount(text, 260);
            bookMeta.setPages(pages);

            book.setItemMeta(bookMeta);
        }

        player.openBook(book);
    }

    @Override
    public DisplayItemStack getPreviewItem() {
        DisplayItemStack.Builder previewItem = LushMail.getInstance().getConfigManager().getGuiItemBuilder("text-mail");
        if (previewItem != null) {
            String displayName = previewItem.getDisplayName();
            if (displayName != null) {
                previewItem.setDisplayName(displayName.replace("%title%", this.getTitle()));
            }
        } else {
            previewItem = new DisplayItemStack.Builder(Material.WRITABLE_BOOK);
            previewItem.setDisplayName("&f" + this.getTitle());
        }

        previewItem.setLore(StringUtils.splitByCount(StringUtils.shortenString(text, 130), 50).stream().map(str -> "&7&o" + str).toList());

        return previewItem.build();
    }
}
