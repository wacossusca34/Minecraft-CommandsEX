package com.github.zathrus_writer.commandsex.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.FileListHelper;
import com.github.zathrus_writer.commandsex.helpers.ReplacementPair;

public class Handler_replacechat implements Listener {

	public static List<ReplacementPair> pairs = new ArrayList<ReplacementPair>();
	public static boolean allUpper = true;
	
	/***
	 * Tells our main class which function we want to execute on PlayerChatEvent
	 * and loads existing chat replacements from the config file.
	 * @param plugin
	 */
	public Handler_replacechat() {
		// load replacement values from config file
		File playerChatFile = new File(CommandsEX.plugin.getDataFolder(), CommandsEX.plugin.getConfig().getString("chatReplaceFile"));
		FileListHelper.checkListFile(playerChatFile, "playerchat.txt");
		pairs = FileListHelper.loadListFromFile(playerChatFile);
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	public static void addReplacementPair(ReplacementPair pair) {
		pairs.add(pair);
	}
	
	public static void clearReplacementPairs() {
		pairs.clear();	
	}
	
	/***
	 * The main function which replaces chat with matched replacements.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void replaceChat(PlayerChatEvent e) {
		if (e.isCancelled()) return;
		for (ReplacementPair rp : pairs) {
			StringBuffer sb = new StringBuffer();
			Matcher m = rp.getRegex().matcher(e.getMessage());
			if (!m.find()) continue;
			//loop through with find/replace
			do { //use do while, due to the find() invocation above
				// test if it is all upper, and replace with all upper (if we have this set up in the regex itself - in config file)
				if (rp.getSameOutputCase() && allUpper && m.group().toUpperCase().equals(m.group())) {
					m.appendReplacement(sb, rp.getReplacement().toUpperCase());
				} else {
					m.appendReplacement(sb, rp.getReplacement());
				}
			} while (m.find());
			m.appendTail(sb);
			e.setMessage(sb.toString());
		}
	}
	
}
