package org.lushplugins.lushmail.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.lushplugins.lushlib.gui.inventory.SimpleGui;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.storage.StorageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class MailGui extends SimpleGui {

    public MailGui(Player player) {
        super(LushMail.getInstance().getConfigManager().getMailGuiFormat(), "Mail", player);
    }

    @Override
    public void refresh() {
        super.refresh();

        PriorityQueue<Integer> slots = new PriorityQueue<>(LushMail.getInstance().getConfigManager().getMailGuiFormat().getSlotMap().get('M'));
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

                    List<String> lore = item.getLore() != null ? new ArrayList<>(item.getLore()) : new ArrayList<>();
                    DisplayItemStack.Builder previewItem = DisplayItemStack.Builder.of(item);
                    DisplayItemStack previewLayout = LushMail.getInstance().getConfigManager().getGuiItem("text-mail");
                    if (previewLayout != null) {
                        List<String> previewLore = previewLayout.getLore();
                        if (previewLore != null) {
                            lore.addAll(previewLore);
                            lore.replaceAll((line) -> line.replace("%mail_id%", mailId));
                        }

                        previewItem.setLore(lore);
                    }

                    ItemStack button = previewItem.build().asItemStack(this.getPlayer());
                    setItem(slot, button); // TODO: Remove once addButton is fixed
                    addButton(slot, button, (event) -> {
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
