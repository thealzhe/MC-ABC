package tv.alzhe.abc;

import java.io.File;
// To bypass Java Error in console?
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import tv.alzhe.abc.Commands.DefaultCommands;
import tv.alzhe.abc.Commands.PluginCommands;
import tv.alzhe.abc.Commands.Common.BaseCommandExecutor;
import tv.alzhe.abc.Commands.Common.CommandManager;
import tv.alzhe.abc.Models.Message;
import tv.alzhe.abc.Models.MessageList;
import tv.alzhe.abc.Models.MessageLists;

public class AutoBroadcaster extends JavaPlugin {
	public static AutoBroadcaster plugin;

	@Override
	public void onEnable() {
		plugin = this;

		// Setup the base command.
		getCommand("autobroadcaster").setExecutor(new BaseCommandExecutor());

		// Register additional commands.
		CommandManager.register(DefaultCommands.class);
		CommandManager.register(PluginCommands.class);

		// Load the configuration.
		loadConfig();
	}

	@Override
	public void onDisable() {
		MessageLists.unschedule();

		plugin = null;
	}

	public boolean loadConfig() {
		if (!(new File(getDataFolder() + File.separator + "config.yml").exists())) {
			saveDefaultConfig();
		}


		try {
			new YamlConfiguration().load(new File(getDataFolder() + File.separator + "config.yml"));
		} catch (Exception e) {
			System.out.println("--- --- --- ---");
			System.out.println("We could not read config.yml correctly. An error has been found.");
			System.out.println("A detailed description of your error is shown below.");
			System.out.println("--- --- --- ---");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);

			return false;
		}

		reloadConfig();

		MessageLists.clear();

		for (String key : getConfig().getConfigurationSection("message-lists").getKeys(false)) {
			MessageList list = new MessageList();

			if (getConfig().contains("message-lists." + key + ".enabled"))
				list.setEnabled(getConfig().getBoolean("message-lists." + key + ".enabled"));

			if (getConfig().contains("message-lists." + key + ".interval"))
				list.setInterval(getConfig().getInt("message-lists." + key + ".interval"));

			if (getConfig().contains("message-lists." + key + ".expiry"))
				list.setExpiry(getConfig().getLong("message-lists." + key + ".expiry"));

			if (getConfig().contains("message-lists." + key + ".random"))
				list.setRandom(getConfig().getBoolean("message-lists." + key + ".random"));

			LinkedList<Message> finalMessages = new LinkedList<Message>();

			if (getConfig().contains("message-lists." + key + ".messages")) {
				ArrayList<Object> messages = (ArrayList<Object>) getConfig().getList("message-lists." + key + ".messages");

				for (Object m : messages) {
					if (m instanceof String) {
						finalMessages.add(new Message((String) m));
					} else if (m instanceof Map) {
						Map<String, List<String>> message = (Map<String, List<String>>) m;

						for (Entry<String, List<String>> entry : message.entrySet()) {
							finalMessages.add(new Message(entry.getKey()));
						}
					}
				}
			}

			list.setMessages(finalMessages);

			MessageLists.setList(key, list);
		}

		MessageLists.schedule();

		// Saves any version changes to the disk
		saveConfiguration();

		return true;
	}

	public void saveConfiguration() {
		if (!(new File(getDataFolder() + File.separator + "config.yml").exists())) {
			saveDefaultConfig();
		}

		for (String key : getConfig().getConfigurationSection("message-lists").getKeys(false)) {
			getConfig().set("message-lists." + key, null);
		}

		for (String key : MessageLists.getMessageLists().keySet()) {
			MessageList list = MessageLists.getExactList(key);
			getConfig().set("message-lists." + key + ".enabled", list.isEnabled());
			getConfig().set("message-lists." + key + ".interval", list.getInterval());
			getConfig().set("message-lists." + key + ".expiry", list.getExpiry());
			getConfig().set("message-lists." + key + ".random", list.isRandom());

			List<String> messages = new LinkedList<String>();

			for (Message m : list.getMessages()) {
				messages.add(m.getMessage());
			}

			getConfig().set("message-lists." + key + ".messages", messages);
		}

		saveConfig();
	}

	public File getFile() {
		return super.getFile();
	}
}
