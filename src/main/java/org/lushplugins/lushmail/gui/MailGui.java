package org.lushplugins.lushmail.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.gui.inventory.SimpleGui;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.data.ReceivedMail;
import org.lushplugins.lushmail.storage.StorageManager;

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

                storageManager.loadMailPreparedPreviewItem(mailId).thenAccept(previewItem -> {
                    Integer slot = slots.poll();
                    if (slot == null) {
                        return;
                    }

                    ItemStack button = previewItem.asItemStack();
                    setItem(slot, button); // TODO: Remove once addButton is fixed
                    addButton(slot, button, (event) -> {
                        switch (event.getClick()) {
                            case RIGHT -> storageManager.loadMail(mailId).thenAccept(loadedMail -> loadedMail.preview(this.getPlayer()));
                            case LEFT -> {
                                if (this.getPlayer().getUniqueId().equals(mailUserUuid)) {
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
