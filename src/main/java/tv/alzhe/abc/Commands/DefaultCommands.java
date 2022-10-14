package tv.alzhe.abc.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import tv.alzhe.abc.Permission;
import tv.alzhe.abc.AutoBroadcaster;
import tv.alzhe.abc.Commands.Common.BaseCommand;
import tv.alzhe.abc.Commands.Common.CommandManager;
import tv.alzhe.abc.Util.Utils;

public class DefaultCommands {

	@BaseCommand(aliases = {"help", "?"}, desc = "View all commands and their info.", usage = "[Page]", permission = Permission.NONE, min = 0, max = 1, hidden = true)
	public void helpCmd(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		// help [Page]
		int maxLines = 6;

		if (args.length != 0) {
			if (!(Utils.isInteger(args[0])) || Math.abs(Integer.valueOf(args[0])) * maxLines - maxLines >= CommandManager.getCommands().size()) {
				sender.sendMessage(ChatColor.RED + "The specified page was not found.");
				return;
			}
		}

		int page = args.length == 0 ? 1 : Math.abs(Integer.valueOf(args[0]));
		int total = 0;
		sender.sendMessage(CommandManager.getExtra() + "__________________.[ " + CommandManager.getHighlight() + AutoBroadcaster.plugin.getName() + CommandManager.getExtra() + " ].__________________");

		sender.sendMessage(ChatColor.GRAY + "Required: < > Optional: [ ]");
		for (int i = maxLines * page - maxLines; i < CommandManager.getCommands().size() && total < maxLines - 1; i++) {
			BaseCommand command = CommandManager.getCommands().get(i);
			if (!(command.hidden()) && Permission.has(command.permission(), sender)) {
				sender.sendMessage(CommandManager.getExtra() + " - " + CommandManager.getDark() + "/" + commandLabel + " " + command.aliases()[0] + (!(command.usage().equals("")) ? " " + command.usage() : "") + ": " + CommandManager.getLight() + command.desc());
				total++;
			}
		}
		sender.sendMessage(CommandManager.getLight() + "For help type: " + CommandManager.getHighlight() + "/" + commandLabel + " help [Page]");
		sender.sendMessage(CommandManager.getExtra() + "---------------------------------------------------");
	}

}
