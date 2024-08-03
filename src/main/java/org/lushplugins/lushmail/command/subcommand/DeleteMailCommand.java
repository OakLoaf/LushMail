package org.lushplugins.lushmail.command.subcommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushlib.command.SubCommand;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.storage.StorageManager;

import java.util.List;

public class DeleteMailCommand extends SubCommand {

    public DeleteMailCommand() {
        super("delete");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (!(sender instanceof Player player)) {
            ChatColorHandler.sendMessage(sender, "Only players can use this command");
            return true;
        }

        if (args.length < 1 || args[0].isBlank()) {
            ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("invalid-args", "&cInvalid arguments try: %command%")
                .replace("%command%", "/mail delete <mail_id>"));
            return true;
        }

        String mailId = args[0].strip();
        StorageManager storageManager = LushMail.getInstance().getStorageManager();
        if (!mailId.equals("all")) {
            storageManager.hasReceivedMail(player.getUniqueId(), mailId).thenAccept(received -> {
                if (!received) {
                    ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("mail-not-found", "&cCould not find this mail in your mail list"));
                    return;
                }

                storageManager.removeMailFor(player.getUniqueId(), mailId).thenAccept(ignored -> {
                    ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("deleted-mail", "&aRemoved mail '%mail_id%' from your mail list!")
                        .replace("%mail_id%", mailId));
                });
            });
        } else {
            storageManager.getReceivedMailIds(player.getUniqueId()).thenAccept(ids -> {
                if (ids.isEmpty()) {
                    ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("no-mail", "&cCould not find any mail in your mail list"));
                    return;
                }

                for (String id : ids) {
                    // TODO: Consider adding remove all mail method
                    storageManager.removeMailFor(player.getUniqueId(), id);
                }

                ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("deleted-all-mail", "&aYour mail list has been cleared!"));
            });
        }

        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        return args.length == 1 ? List.of("all") : null;
    }
}
