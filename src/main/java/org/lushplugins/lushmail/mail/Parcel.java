package org.lushplugins.lushmail.mail;

import org.bukkit.entity.Player;
import org.lushplugins.lushlib.utils.SimpleItemStack;

import java.util.List;

public class Parcel extends Mail {
    private final List<Mail> mail;

    public Parcel(String id, List<Mail> mail, String sender) {
        super(id, "text", sender, "Package from " + sender);
        this.mail = mail;
    }

    public Parcel(String id, List<Mail> mail, String sender, String title) {
        super(id, "parcel", sender, title);
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
    public void preview(Player player) {

    }

    @Override
    public SimpleItemStack getPreviewItem() {
        return null;
    }
}
