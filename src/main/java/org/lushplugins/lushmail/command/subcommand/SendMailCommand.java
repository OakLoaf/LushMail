package org.lushplugins.lushmail.command.subcommand;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.SubCommand;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.mail.TextMail;
import org.lushplugins.lushmail.storage.StorageManager;

import java.util.List;

public class SendMailCommand extends SubCommand {

    public SendMailCommand() {
        super("send");
        addRequiredArgs(0, () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        addRequiredArgs(1, () -> List.of("<message>"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (args.length < 2 || args[1].isBlank()) {
            // TODO: Add configurable message
            ChatColorHandler.sendMessage(sender, "Invalid arguments try: /mail send <username> <message>");
            return true;
        }

        String message = args[1].strip();
        StorageManager storageManager = LushMail.getInstance().getStorageManager();
        storageManager.loadUniqueId(args[0]).thenAccept(receiver -> {
            if (receiver == null) {
                // TODO: Add configurable message
                ChatColorHandler.sendMessage(sender, "Could not find this player");
                return;
            }

            LushMail.getInstance().getMailManager().canSendMailTo(sender, receiver).thenAccept(canSend -> {
                if (!canSend) {
                    // TODO: Add configurable message
                    ChatColorHandler.sendMessage(sender, "You cannot send mail to this player");
                    return;
                }

                LushMail.getInstance().getMailManager().generateUniqueMailId().thenAccept(id -> {
                    if (id == null) {
                        // TODO: Add configurable message
                        ChatColorHandler.sendMessage(sender, "Something went wrong when trying to send mail");
                        return;
                    }

                    String senderName = sender instanceof Player player ? player.getUniqueId().toString() : "console";
                    storageManager.saveMail(new TextMail(id, message))
                        .thenAccept(ignored -> storageManager.sendMail(senderName, receiver, id));
                });
            });
        });

        return true;
    }
}
