package de.rpkak.dbu.command;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.rpkak.dbu.command.manage.Command;
import de.rpkak.dbu.command.manage.GuildCommand;
import de.rpkak.dbu.listeners.CommandListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * The {@link HelpCommand} that shows all Commands and what they do.
 * 
 * @author rpkak
 *
 */
public class HelpCommand extends Command {

	private final CommandListener commandListener;
	private final String generalInformation;

	/**
	 * Creates a new instance of a {@link HelpCommand}.
	 * 
	 * @param name        the main name of the command
	 * @param aliases     the aliases to call this command
	 * @param description the description that is shown in the {@link HelpCommand}
	 */
	public HelpCommand(String name, String[] aliases, String description, CommandListener commandListener,
			String generalInformation) {
		super(name, aliases, description);
		this.commandListener = commandListener;
		this.generalInformation = generalInformation;
	}

	/**
	 * Creates a new default instance of a {@link HelpCommand}.
	 */
	public HelpCommand(CommandListener commandListener, String generalInformation) {
		super("Help", new String[] { "help", "h" },
				"Gives you the description of my commands\n```\n| help\n> Gives you the description of all commands\n\n| help [Command]\n> Gives you the description of that command\n\n| help --guild\n> Gives you the description of all guild commands\n```\nYou can always use ``h`` instead of ``help``.");
		this.commandListener = commandListener;
		this.generalInformation = generalInformation;

	}

	private String getPrefixes() {
		if (commandListener.getprefixes().length == 1) {
			return "The Bot's prefix is " + commandListener.getprefixes()[0] + ".";
		} else {
			return "The Bot's prefixes are "
					+ String.join(", ",
							Arrays.copyOfRange(commandListener.getprefixes(), 0,
									commandListener.getprefixes().length - 1))
					+ " and " + commandListener.getprefixes()[commandListener.getprefixes().length - 1] + ".";
		}
	}

	@Override
	public void onCommand(MessageReceivedEvent event, List<String> commandContent) throws Throwable {
		if (commandContent.size() == 0) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setDescription(getPrefixes());
			builder.addField("General Information", generalInformation, false);
			for (Command command : commandListener.getCommands()) {
				addCommandToEmbed(command, builder);
			}
			builder.setAuthor("Help");
			builder.setColor(Color.GREEN);
			event.getChannel().sendMessage(builder.build()).queue();
		} else if (commandContent.get(0).equalsIgnoreCase("--guild")) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setDescription(getPrefixes());
			builder.addField("General Information", generalInformation, false);
			for (Command command : commandListener.getCommands()) {
				if (command instanceof GuildCommand) {
					addCommandToEmbed(command, builder);
				}
			}
			builder.setAuthor("Server-Help");
			builder.setColor(Color.GREEN);
			event.getChannel().sendMessage(builder.build()).queue();
		} else {
			List<Command> commands = new ArrayList<Command>();
			for (Command command : commandListener.getCommands()) {
				for (String string : commandContent) {
					boolean ic = command.getName().equalsIgnoreCase(string);
					if (!ic) {
						for (String alias : command.getAliases()) {
							if (alias.equalsIgnoreCase(string)) {
								ic = true;
								break;
							}
						}
					}
					if (ic) {
						commands.add(command);
					}
				}
			}
			if (commands.isEmpty()) {
				EmbedBuilder builder = new EmbedBuilder();
				builder.setAuthor("Not foun't");
				builder.setColor(Color.RED);
				builder.setDescription("No Command(s) foun't: \"" + String.join("\", \"", commandContent) + "\" ("
						+ event.getAuthor().getAsMention() + ").");
				event.getChannel().sendMessage(builder.build()).queue();
			} else {
				EmbedBuilder builder = new EmbedBuilder();
				builder.setDescription(getPrefixes());
				builder.addField("General Information", generalInformation, false);
				for (Command command : commands) {
					addCommandToEmbed(command, builder);
				}
				builder.setColor(Color.GREEN);
				builder.setAuthor("Help");
				event.getChannel().sendMessage(builder.build()).queue();
			}
		}
	}

	private void addCommandToEmbed(Command command, EmbedBuilder embed) {
		String name = command.getName();
		if (command instanceof GuildCommand) {
			GuildCommand guildCommand = (GuildCommand) command;
			if (guildCommand.getPermissions().length == 0) {
				name += " (Server only)";
			} else {
				name += " (Server only; Permissions: " + String.join(", ", Arrays.stream(guildCommand.getPermissions())
						.map(Permission::getName).collect(Collectors.toList())) + ")";
			}
		}
		embed.addField(name, command.getDescription(), false);
	}
}
