package org.lushplugins.lushmail.mail;

import org.bukkit.entity.Player;
import org.lushplugins.lushlib.utils.SimpleItemStack;

import java.util.List;

public class Parcel extends Mail {
    private final List<Mail> mail;

    public Parcel(String id, List<Mail> mail) {
        super(id, "parcel");
        this.mail = mail;
    }

    @Override
    public void open(Player player) {
        for (Mail mail : this.mail) {
            try {
                mail.open(player);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public SimpleItemStack getPreviewItem() {
        return null;
    }
}
