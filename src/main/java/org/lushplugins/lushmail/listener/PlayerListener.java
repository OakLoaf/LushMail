package org.lushplugins.lushmail.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.lushplugins.lushlib.listener.EventListener;
import org.lushplugins.lushmail.LushMail;

public class PlayerListener implements EventListener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LushMail.getInstance().getMailManager().getUnopenedGroupMailIds(player).thenAccept(mailIds -> {

        });
    }
}
