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

import java.util.Arrays;
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
            ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("invalid-args", "&cInvalid arguments try: %command%")
                .replace("%command%", "/mail send <username> <message>"));
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).strip();
        StorageManager storageManager = LushMail.getInstance().getStorageManager();
        storageManager.loadUniqueId(args[0]).thenAccept(receiverUuid -> {
            if (receiverUuid == null) {
                ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("invalid-player", "&cCould not find player '%player%'")
                    .replace("%player%", args[0]));;
                return;
            }

            LushMail.getInstance().getMailManager().canSendMailTo(sender, receiverUuid).thenAccept(canSend -> {
                if (!canSend) {
                    ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("ignored", "&cYou cannot send mail to %player%")
                        .replace("%player%", args[0]));
                    return;
                }

                LushMail.getInstance().getMailManager().generateUniqueMailId().thenAccept(id -> {
                    if (id == null) {
                        ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("failed-to-send-mail", "&cSomething went wrong whilst trying to send mail"));
                        return;
                    }

                    String senderId;
                    String senderName;
                    if (sender instanceof Player player) {
                        senderId = player.getUniqueId().toString();
                        senderName = player.getName();
                    } else {
                        senderId = "console";
                        senderName = LushMail.getInstance().getConfigManager().getConsoleName();
                    }

                    storageManager.saveMail(new TextMail(id, message, senderName))
                        .thenAccept(ignored -> storageManager.sendMail(senderId, receiverUuid, id));

                    ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("sent-mail", "&aSent mail to %receiver%")
                        .replace("%receiver%", args[0]));

                    Player receiver = Bukkit.getPlayer(receiverUuid);
                    if (receiver != null) {
                        ChatColorHandler.sendMessage(sender, LushMail.getInstance().getConfigManager().getMessage("received-mail", "&aYou have received new mail from %sender%!")
                            .replace("%sender%", senderName));
                    }
                });
            });
        });

        return true;
    }
}
