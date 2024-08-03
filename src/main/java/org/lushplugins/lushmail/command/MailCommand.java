package org.lushplugins.lushmail.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.Command;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushmail.command.subcommand.*;
import org.lushplugins.lushmail.gui.MailGui;

public class MailCommand extends Command {

    public MailCommand() {
        super("mail");
        addSubCommand(new DeleteMailCommand());
        addSubCommand(new IgnoreUserCommand());
        addSubCommand(new OpenMailCommand());
        addSubCommand(new ReloadSubCommand());
        addSubCommand(new SendMailCommand());
        addSubCommand(new UnignoreUserCommand());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (!(sender instanceof Player player)) {
            ChatColorHandler.sendMessage(sender, "Only players can use this command");
            return true;
        }

        new MailGui(player).open();

        return true;
    }
}
