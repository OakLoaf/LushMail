package org.lushplugins.lushmail.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.lushplugins.lushlib.gui.inventory.GuiFormat;
import org.lushplugins.lushlib.gui.inventory.SimpleGui;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushlib.utils.SimpleItemStack;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.storage.StorageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class MailGui extends SimpleGui {
    private static final GuiFormat GUI_FORMAT = new GuiFormat(
        "MMMMMMMMM",
        "MMMMMMMMM",
        "MMMMMMMMM",
        "MMMMMMMMM",
        "MMMMMMMMM",
        "#########"
    );

    static {
        SimpleItemStack border =  new SimpleItemStack(Material.WHITE_STAINED_GLASS_PANE);
        border.setDisplayName(ChatColorHandler.translate("&7"));
        GUI_FORMAT.setItemReference('#', border);
    }

    public MailGui(Player player) {
        super(GUI_FORMAT, "Mail", player);
    }

    @Override
    public void refresh() {
        super.refresh();

        PriorityQueue<Integer> slots = new PriorityQueue<>(GUI_FORMAT.getSlotMap().get('M'));
        // TODO: Adjust to query that accepts indexes (for pagination)
        LushMail.getInstance().getMailManager().getAllUnopenedMailIds(this.getPlayer()).thenAccept(mailIds -> {
            StorageManager storageManager = LushMail.getInstance().getStorageManager();

            for (String mailId : mailIds) {
                if (slots.isEmpty()) {
                    break;
                }

                storageManager.loadMailPreviewItem(mailId).thenAccept(item -> {
                    Integer slot = slots.poll();
                    if (slot == null) {
                        return;
                    }

                    List<String> lore = item.getLore() != null ? item.getLore() : new ArrayList<>();
                    lore.add(" ");
                    lore.add("&#ffde8aʟᴇғᴛ ᴄʟɪᴄᴋ &7- Open mail");
                    lore.add("&#ffde8aʀɪɢʜᴛ ᴄʟɪᴄᴋ &7- Preview mail");
                    lore.add(" ");
                    lore.add("&8Mail ID: " + mailId);
                    item.setLore(lore);

                    setItem(slot, item.asItemStack(this.getPlayer())); // TODO: Remove once addButton is fixed
                    addButton(slot, item.asItemStack(this.getPlayer()), (event) -> {
                        switch (event.getAction()) {
                            case PICKUP_HALF -> storageManager.loadMail(mailId).thenAccept(mail -> mail.preview(this.getPlayer()));
                            case PICKUP_ALL -> storageManager.loadMail(mailId).thenAccept(mail -> mail.open(this.getPlayer()));
                        }
                    });
                });
            }
        });
    }
}
