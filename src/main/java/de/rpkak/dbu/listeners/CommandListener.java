package de.rpkak.dbu.listeners;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.rpkak.dbu.Utils;
import de.rpkak.dbu.command.HelpCommand;
import de.rpkak.dbu.command.manage.Command;
import de.rpkak.dbu.command.manage.GuildCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * an instance of a {@link ListenerAdapter} that cares about {@link Command}s
 * (for example the {@link HelpCommand} or {@link GuildCommand}s)
 * 
 * @author rpkak
 *
 */
public class CommandListener extends ListenerAdapter {

	private String[] prefixes;
	private final List<Command> commands = new ArrayList<Command>();

	/**
	 * Creates a new instance of a {@link CommandListener}
	 * 
	 * @param prefixes the prefixes like "!foo"
	 */
	public CommandListener(String... prefixes) {
		this.prefixes = prefixes;
	}

	/**
	 * Returns the prefixes
	 * 
	 * @return the prefixes
	 */
	public String[] getprefixes() {
		return prefixes;
	}

	/**
	 * Adds a new {@link Command}
	 * 
	 * @param command the {@link Command} to add
	 */
	public void addCommand(Command command) {
		commands.add(command);
	}

	/**
	 * looks if there is any {@link Command} called.
	 */
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		String content = event.getMessage().getContentRaw();
		List<String> splited = split(content);
		if (!event.getAuthor().isBot()) {
			if (splited.size() > 1) {
				for (String prefix : prefixes) {
					if (splited.get(0).equalsIgnoreCase(prefix)) {
						boolean used = false;
						for (Command command : commands) {
							for (String alias : command.getAliases()) {
								if (alias.equalsIgnoreCase(splited.get(1))) {
									used = true;
									event.getChannel().sendTyping().queue(Void -> {
										Utils.tryCatch(
												() -> command.onCommand(event, splited.subList(2, splited.size())),
												event.getChannel(),
												"Error while handling the command \"" + content + "\".",
												event.getGuild());
									});
								}
							}
						}
						if (!used) {
							EmbedBuilder builder = new EmbedBuilder();
							builder.setAuthor("Not foun't");
							builder.setColor(Color.RED);
							builder.setDescription("No Command foun't: \"" + splited.get(1) + "\" ("
									+ event.getAuthor().getAsMention() + ").");
							event.getChannel().sendMessage(builder.build()).queue();
						}
					}
				}
			}
		}
	}

	private List<String> split(String in) {
		List<String> words = new ArrayList<String>();
		String word = "";
		int mode = 0;
		for (char c : in.toCharArray()) {
			if (c == ' ' && mode == 0) {
				if (!word.equals("")) {
					words.add(word);
					word = "";
				}
			} else if (c == '\'' && mode != 2) {
				if (mode == 0) {
					mode = 1;
				} else if (mode == 1) {
					mode = 0;
				}
			} else if (c == '\"' && mode != 1) {
				if (mode == 0) {
					mode = 2;
				} else if (mode == 2) {
					mode = 0;
				}
			} else {
				word += c;
			}
		}
		if (!word.equals("")) {
			words.add(word);
		}
		return words;
	}

	/**
	 * Returns all {@link Command}s
	 * 
	 * @return all {@link Command}s
	 */
	public List<Command> getCommands() {
		return commands;
	}
}
