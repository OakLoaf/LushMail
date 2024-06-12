package org.lushplugins.lushmail.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushlib.listener.EventListener;
import org.lushplugins.lushmail.LushMail;

public class PlayerListener implements EventListener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(LushMail.getInstance(), () -> {
            LushMail.getInstance().getMailManager().getAllUnopenedMailIds(player).thenAccept(mailIds -> {
                int mailCount = mailIds.size();
                if (mailCount > 0) {
                    // TODO: Add configurable message
                    ChatColorHandler.sendMessage(player, "You have %count% new mail"
                        .replace("%count%", String.valueOf(mailCount)));
                }
            });
        }, 40);
    }
}
