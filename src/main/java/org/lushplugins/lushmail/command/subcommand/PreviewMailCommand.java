package org.lushplugins.lushmail.command.subcommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.SubCommand;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.gui.PreviewGui;
import org.lushplugins.lushmail.mail.Mail;
import org.lushplugins.lushmail.storage.StorageManager;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PreviewMailCommand extends SubCommand {

    public PreviewMailCommand() {
        super("preview");
        addRequiredPermission("lushmail.preview");
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
        String mailId = args[0].strip();
        StorageManager storageManager = LushMail.getInstance().getStorageManager();

        CompletableFuture<Mail> mailFuture = new CompletableFuture<>();
        mailFuture.thenAccept(mail -> {
            if (mail == null) {
                ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("mail-not-found", "&cCould not find this mail"));
                return;
            }

            new PreviewGui(mail, player).open();
        });

        if (!player.hasPermission("lushmail.preview.others")) {
            storageManager.hasReceivedMail(receiver, mailId).thenAccept((hasReceived) -> {
                if (hasReceived) {
                    storageManager.loadMail(mailId).thenAccept(mailFuture::complete);
                } else {
                    mailFuture.complete(null);
                }
            });
        } else {
            storageManager.loadMail(mailId).thenAccept(mailFuture::complete);
        }

        return true;
    }
}
