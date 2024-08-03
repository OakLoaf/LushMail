package org.lushplugins.lushmail.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushlib.listener.EventListener;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.data.OfflineMailUser;
import org.lushplugins.lushmail.storage.StorageManager;

public class PlayerListener implements EventListener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(LushMail.getInstance(), () -> {
            StorageManager storageManager = LushMail.getInstance().getStorageManager();
            storageManager.loadOfflineMailUser(player.getUniqueId()).thenAccept(mailUser -> {
                if (mailUser == null || !mailUser.getUsername().equals(player.getName())) {
                    storageManager.saveOfflineMailUser(new OfflineMailUser(player.getUniqueId(), player.getName()));
                }
            });

            LushMail.getInstance().getMailManager().getAllUnopenedMailIds(player).thenAccept(mailIds -> {
                int mailCount = mailIds.size();
                if (mailCount > 0) {
                    ChatColorHandler.sendMessage(player, LushMail.getInstance().getConfigManager().getMessage("received-offline-mail", "&aYou have %count% new mail")
                        .replace("%count%", String.valueOf(mailCount)));
                }
            });
        }, 40);
    }
}
