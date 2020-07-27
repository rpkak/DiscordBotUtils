package de.rpkak.dbu.command.manage;

import java.util.List;

import de.rpkak.dbu.command.HelpCommand;
import de.rpkak.dbu.listeners.CommandListener;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * A Command that anyone can call.
 * 
 * @author rpkak
 *
 */
public abstract class Command {
	private final String[] aliases;
	private final String description;
	private final String name;

	/**
	 * Creates a new instance of a {@link Command}.
	 * 
	 * @param name        the main name of the command
	 * @param aliases     the aliases to call this command
	 * @param description the description that is shown in the {@link HelpCommand}
	 */
	public Command(String name, String[] aliases, String description) {
		this.name = name;
		this.aliases = aliases;
		this.description = description;
	}

	/**
	 * Returns the name of the command.
	 * 
	 * @return the name of the command
	 */

	public String getName() {
		return name;
	}

	/**
	 * Returns the aliases of the command.
	 * 
	 * @return the aliases of the command
	 */
	public String[] getAliases() {
		return aliases;
	}

	/**
	 * Returns the description of the command.
	 * 
	 * @return the description of the command
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * This is the event function you should override.
	 * 
	 * @param event          the {@link MessageReceivedEvent} that is fired then
	 *                       somebody called this command
	 * @param commandContent a {@link List} of contend strings that are after the
	 *                       {@link CommandListener#getPrefixs()} and the
	 *                       {@link #names} of the event. If you use them you don't
	 *                       must split the contend yourself.
	 * @throws Throwable If there are Throwables in your code the
	 *                   {@link CommandListener} will catch them for you.
	 */
	public abstract void onCommand(MessageReceivedEvent event, List<String> commandContent) throws Throwable;
}
