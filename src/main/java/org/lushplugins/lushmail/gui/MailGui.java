package org.lushplugins.lushmail.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.gui.inventory.SimpleGui;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.data.ReceivedMail;
import org.lushplugins.lushmail.storage.StorageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;

public class MailGui extends SimpleGui {
    private final UUID mailUserUuid;

    public MailGui(Player player) {
        this(player.getUniqueId(), player);
    }

    public MailGui(@NotNull UUID mailUserUuid, @NotNull Player viewer) {
        super(LushMail.getInstance().getConfigManager().getMailGuiFormat(), "Mail", viewer);
        this.mailUserUuid = mailUserUuid;
    }

    @Override
    public void refresh() {
        super.refresh();

        PriorityQueue<Integer> slots = new PriorityQueue<>(LushMail.getInstance().getConfigManager().getMailGuiFormat().getSlotMap().get('M'));
        // TODO: Adjust to query that accepts indexes (for pagination)
        LushMail.getInstance().getMailManager().getAllUnopenedMailIds(mailUserUuid).thenAccept(mailIds -> {
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
                    DisplayItemStack previewLayout = LushMail.getInstance().getConfigManager().getGuiItem("preview-mail");
                    if (previewLayout != null) {
                        List<String> previewLore = previewLayout.getLore();
                        if (previewLore != null) {
                            lore.addAll(previewLore);
                            lore.replaceAll((line) -> line.replace("%mail_id%", mailId));
                        }

                        previewItem.setLore(lore);
                    }

                    ItemStack button = previewItem.build().asItemStack();
                    setItem(slot, button); // TODO: Remove once addButton is fixed
                    addButton(slot, button, (event) -> {
                        switch (event.getClick()) {
                            case RIGHT -> storageManager.loadMail(mailId).thenAccept(mail -> mail.preview(this.getPlayer()));
                            case LEFT -> {
                                if (!this.getPlayer().getUniqueId().equals(mailUserUuid)) {
                                    storageManager.getReceivedMail(mailUserUuid, mailId).thenAccept(ReceivedMail::open);
                                }
                            }
                        }
                    });
                });
            }
        });
    }
}
