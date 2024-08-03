package org.lushplugins.lushmail.command.subcommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.SubCommand;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushmail.LushMail;

import java.util.logging.Level;

public class ReloadSubCommand extends SubCommand {

    public ReloadSubCommand() {
        super("reload");
        addRequiredPermission("lushmail.reload");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        try {
            LushMail.getInstance().getConfigManager().reloadConfig();
            ChatColorHandler.sendMessage(sender, "&#b7faa2LushMail has been reloaded &#66b04f🔃");
        } catch (Exception e) {
            ChatColorHandler.sendMessage(sender, "&#ff6969Something went wrong when reloading, check the console for errors");
            LushMail.getInstance().log(Level.WARNING, "Caught error whilst reloading config:", e);
        }

        return true;
    }
}
