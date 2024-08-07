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

public class IgnoreUserCommand extends SubCommand {

    public IgnoreUserCommand() {
        super("ignore");
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
                .replace("%command%", "/mail ignore <username>"));
            return true;
        }

        StorageManager storageManager = LushMail.getInstance().getStorageManager();
        storageManager.loadUniqueId(args[0]).thenAccept(toIgnore -> {
            if (toIgnore == null) {
                ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("invalid-args", "&cInvalid arguments try: %command%")
                    .replace("%command%", "/mail ignore <username>"));
                return;
            }

            storageManager.canSendMailTo(toIgnore, player.getUniqueId()).thenAccept(canSend -> {
                if (!canSend) {
                    ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("already-ignoring", "You are already ignoring %player%")
                        .replace("%player%", args[0]));
                    return;
                }

                storageManager.removeIgnoredUser(player.getUniqueId(), toIgnore).thenAccept(ignored -> {
                    ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("ignore-player", "You will no longer receive mail from %player%")
                        .replace("%player%", args[0]));
                });
            });
        });

        return true;
    }
}
