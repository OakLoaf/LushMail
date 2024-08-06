package org.lushplugins.lushmail.command.subcommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushlib.command.SubCommand;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushmail.LushMail;

import java.util.List;

/**
 * Regenerates the preview item of mail
 */
public class RegenerateMailCommand extends SubCommand {

    public RegenerateMailCommand() {
        super("regenerate");
        addRequiredPermission("lushmail.regenerate");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (args.length == 0 || !args[0].equalsIgnoreCase("confirm")) {
            ChatColorHandler.sendMessage(sender, "&#66b04fType &#e0c01b/mail regenerate confirm &#66b04fto confirm!");
            return true;
        }

        LushMail.getInstance().getStorageManager().regenerateMailPreviewItems().thenAccept((ignored) -> {
            ChatColorHandler.sendMessage(sender, "&#b7faa2Successfully regenerated mail preview items!");
        });

        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        return args.length == 1 ? List.of("confirm") : null;
    }
}
