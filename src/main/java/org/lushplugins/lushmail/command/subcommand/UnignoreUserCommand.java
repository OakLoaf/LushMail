package org.lushplugins.lushmail.command.subcommand;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.SubCommand;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.storage.StorageManager;

public class UnignoreUserCommand extends SubCommand {

    public UnignoreUserCommand() {
        super("unignore");
        addRequiredArgs(0, () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (!(sender instanceof Player player)) {
            ChatColorHandler.sendMessage(sender, "Only players can use this command");
            return true;
        }

        if (args.length < 1) {
            ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("invalid-args", "&cInvalid arguments try: %command%")
                .replace("%command%", "/mail unignore <username>"));
            return true;
        }

        StorageManager storageManager = LushMail.getInstance().getStorageManager();
        storageManager.loadUniqueId(args[0]).thenAccept(toIgnore -> {
            if (toIgnore == null) {
                ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("invalid-player", "&cCould not find player '%player%'")
                    .replace("%player%", args[0]));
                return;
            }

            storageManager.canSendMailTo(toIgnore, player.getUniqueId()).thenAccept(canSend -> {
                if (canSend) {
                    ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("not-ignoring", "&cYou are not currently ignoring %player%")
                        .replace("%player%", args[0]));
                    return;
                }

                storageManager.removeIgnoredUser(player.getUniqueId(), toIgnore).thenAccept(ignored -> {
                    ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("unignore-player", "&aYou can now receive mail from %player%")
                        .replace("%player%", args[0]));
                });
            });
        });

        return true;
    }
}
