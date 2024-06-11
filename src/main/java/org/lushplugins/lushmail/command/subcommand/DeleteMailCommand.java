package org.lushplugins.lushmail.command.subcommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.SubCommand;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.storage.StorageManager;

public class DeleteMailCommand extends SubCommand {

    public DeleteMailCommand() {
        super("delete");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (!(sender instanceof Player player)) {
            // TODO: Add configurable message
            ChatColorHandler.sendMessage(sender, "Only players can use this command");
            return true;
        }

        if (args.length < 1 || args[0].isBlank()) {
            // TODO: Add configurable message
            ChatColorHandler.sendMessage(sender, "Invalid arguments try: /mail delete <mail_id>");
            return true;
        }

        String mailId = args[0].strip();
        StorageManager storageManager = LushMail.getInstance().getStorageManager();
        storageManager.hasReceivedMail(player.getUniqueId(), mailId).thenAccept(received -> {
            if (!received) {
                // TODO: Add configurable message
                ChatColorHandler.sendMessage(sender, "Could not find this mail in your mail list");
                return;
            }

            storageManager.removeMailFor(player.getUniqueId(), mailId).thenAccept(ignored -> {
                // TODO: Add configurable message
                ChatColorHandler.sendMessage(sender, "Mail '" + mailId + "' has been removed from your mail list");
            });
        });

        return true;
    }
}
