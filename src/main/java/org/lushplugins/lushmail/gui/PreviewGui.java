package org.lushplugins.lushmail.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.lushplugins.lushlib.gui.inventory.Gui;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.mail.Mail;

public class PreviewGui extends Gui {
    private Mail mail;

    public PreviewGui(Mail mail, Player player) {
        super(InventoryType.DROPPER, "Mail Preview", player);
        this.mail = mail;
    }

    public PreviewGui(String mailId, Player player) {
        super(InventoryType.DROPPER, "Mail Preview", player);
        LushMail.getInstance().getStorageManager().loadMail(mailId).thenAccept(mail -> {
            this.mail = mail;
        });
    }

    @Override
    public void refresh() {
        super.refresh();

        if (mail == null) {
            return;
        }

        ItemStack button = mail.getPreparedPreviewItem().asItemStack();
        setItem(4, button); // TODO: Remove once addButton is fixed
        addButton(4, button, (event) -> mail.preview(this.getPlayer()));
    }
}
