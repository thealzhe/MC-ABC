package tv.alzhe.abc.Tasks;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import tv.alzhe.abc.AutoBroadcaster;
import tv.alzhe.abc.Models.MessageList;
import tv.alzhe.abc.Models.MessageLists;

public class BroadcastTask implements Runnable {
	private final String name;

	public BroadcastTask(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		if (MessageLists.getExactList(name) != null && AutoBroadcaster.plugin.getConfig().getBoolean("settings.enabled")) {
			MessageList list = MessageLists.getExactList(name);

			if (list.isEnabled() && list.hasMessages() && !(list.isExpired())) {
				if (Bukkit.getServer().getOnlinePlayers().size() >= AutoBroadcaster.plugin.getConfig().getInt("settings.min-players")) {
					int index = list.isRandom() ? new Random().nextInt(list.getMessages().size()) : list.getCurrentIndex();

					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						if (p.hasPermission("abc.receive." + name)) {
							list.broadcastTo(index, p);
						}
					}

					if (AutoBroadcaster.plugin.getConfig().getBoolean("settings.log-to-console")) {
						list.broadcastTo(index, Bukkit.getConsoleSender());
					}

					list.setCurrentIndex(index + 1);
				}
			}
		}
	}
}
