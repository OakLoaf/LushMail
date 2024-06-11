package org.lushplugins.lushmail.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.Command;
import org.lushplugins.lushmail.command.subcommand.IgnoreUserCommand;
import org.lushplugins.lushmail.command.subcommand.SendMailCommand;
import org.lushplugins.lushmail.command.subcommand.UnignoreUserCommand;

public class MailCommand extends Command {

    public MailCommand() {
        super("mail");
        addSubCommand(new IgnoreUserCommand());
        addSubCommand(new SendMailCommand());
        addSubCommand(new UnignoreUserCommand());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        // TODO: Open mail gui
        return true;
    }
}
