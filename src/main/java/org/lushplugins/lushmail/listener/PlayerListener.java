package org.lushplugins.lushmail.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushlib.listener.EventListener;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.data.OfflineMailUser;
import org.lushplugins.lushmail.mail.Mail;
import org.lushplugins.lushmail.storage.StorageManager;

import java.util.UUID;

public class PlayerListener implements EventListener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Bukkit.getScheduler().runTaskLater(LushMail.getInstance(), () -> {
            StorageManager storageManager = LushMail.getInstance().getStorageManager();
            storageManager.loadOfflineMailUser(uuid).thenAccept(mailUser -> {
                if (mailUser == null || !mailUser.getUsername().equals(player.getName())) {
                    storageManager.saveOfflineMailUser(new OfflineMailUser(uuid, player.getName()));
                }
            });

            LushMail.getInstance().getMailManager().getReceivedMailIds(uuid, Mail.State.UNOPENED).thenAccept(mailIds -> {
                int mailCount = mailIds.size();
                if (mailCount > 0) {
                    ChatColorHandler.sendMessage(player, LushMail.getInstance().getConfigManager().getMessage("received-offline-mail", "&aYou have %count% new mail")
                        .replace("%count%", String.valueOf(mailCount)));
                }
            });
        }, 40);
    }
}
