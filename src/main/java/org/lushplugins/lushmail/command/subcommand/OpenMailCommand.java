package org.lushplugins.lushmail.command.subcommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.SubCommand;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.mail.Mail;
import org.lushplugins.lushmail.storage.StorageManager;

import java.util.UUID;

public class OpenMailCommand extends SubCommand {

    public OpenMailCommand() {
        super("open");
        addRequiredPermission("lushmail.open");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (!(sender instanceof Player player)) {
            ChatColorHandler.sendMessage(sender, "Only players can use this command");
            return true;
        }

        if (args.length < 1 || args[0].isBlank()) {
            ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("invalid-args", "&cInvalid arguments try: %command%")
                .replace("%command%", "/mail open <mail_id>"));
            return true;
        }

        UUID receiver = player.getUniqueId();
        String mailIdRaw = args[0].strip();
        StorageManager storageManager = LushMail.getInstance().getStorageManager();

        if (mailIdRaw.equals("all")) { // TODO: Consider limiting to non-text mail only
            storageManager.getReceivedMailIds(receiver, Mail.State.UNOPENED).thenAccept(mailIds -> {
                for (String mailId : mailIds) {
                    storageManager.getReceivedMail(receiver, mailId).thenAccept(mail -> {
                        if (mail != null) {
                            mail.open();
                        }
                    });
                }
            });
        } else {
            storageManager.getReceivedMail(receiver, mailIdRaw).thenAccept(mail -> {
                if (mail == null) {
                    ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("mail-not-found", "&cCould not find this mail in your mail list"));
                    return;
                }

                if (mail.getState() .equals(Mail.State.OPENED)) {
                    ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("already-opened-mail", "&cThis mail has already been opened!"));
                    return;
                }

                mail.open();
            });
        }

        return true;
    }
}
