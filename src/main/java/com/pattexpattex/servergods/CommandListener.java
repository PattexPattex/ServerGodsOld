package com.pattexpattex.servergods;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.pattexpattex.servergods.commands.*;
import com.pattexpattex.servergods.commands.status.MaintenanceCommand;
import com.pattexpattex.servergods.commands.status.OfflineCommand;
import com.pattexpattex.servergods.commands.status.OnlineCommand;
import com.pattexpattex.servergods.commands.status.RestartCommand;
import com.pattexpattex.servergods.BotCommand;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {
	private static final Pattern SPACE_PATTERN=Pattern.compile("\\s");
	
	private final String prefix;
	
	private Map<String, BotCommand> commands=new HashMap<>();
	
	public CommandListener(String prefix) {
		this.prefix = prefix;
		commands.put("ping", new PingCommand());
		commands.put("help", new HelpCommand(Collections.unmodifiableMap(commands)));
		commands.put("offline", new OfflineCommand());
		commands.put("online", new OnlineCommand());
		commands.put("maintenance", new MaintenanceCommand());
		commands.put("restarting", new RestartCommand());
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		Message msg = event.getMessage();
		if(msg.getAuthor().isBot()) {
			return;
		}
		String raw = msg.getContentRaw();
		if(!raw.startsWith(prefix)) {
			return;
		}
		String messageWithoutPrefix=raw.substring(prefix.length());
		String[] split=SPACE_PATTERN.split(messageWithoutPrefix);
		String commandName=split[0];
		BotCommand cmd = commands.get(commandName);
		if(cmd==null) {
			msg.reply("Command not found").queue();
		}else {
			String[] args=Stream.of(split).skip(1).toArray(String[]::new);
			cmd.run(event, args);
		}
		
	}
}