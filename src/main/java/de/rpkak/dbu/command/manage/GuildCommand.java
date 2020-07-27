package de.rpkak.dbu.command.manage;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.rpkak.dbu.command.HelpCommand;
import de.rpkak.dbu.listeners.CommandListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author rpkak
 */
public abstract class GuildCommand extends Command {

	private Permission[] permissions;

	/**
	 * Creates a new instance of a {@link GuildCommand}.
	 * 
	 * @param name        the main name of the command
	 * @param aliases     the aliases to call this command
	 * @param description the description that is shown in the {@link HelpCommand}
	 * @param permissions the permissions you must have to execute this command.
	 */
	public GuildCommand(String name, String[] aliases, String description, Permission... permissions) {
		super(name, aliases, description);
		this.permissions = permissions;
	}

	public Permission[] getPermissions() {
		return permissions;
	}

	/**
	 * the overrided function of
	 * {@link Command#onCommand(MessageReceivedEvent, List)} that calls the
	 * {@link #onServerCommand(MessageReceivedEvent, Guild, List)} function
	 */
	@Override
	public void onCommand(MessageReceivedEvent event, List<String> commandContent) throws Throwable {
		if (event.isFromGuild()) {
			if (event.getMember().hasPermission(event.getTextChannel(), permissions)) {
				onServerCommand(event, event.getGuild(), commandContent);
			} else {
				EmbedBuilder builder = new EmbedBuilder();
				builder.setAuthor("Not Allowed");
				String d;
				if (permissions.length == 1) {
					d = "You need to have the Premission " + permissions[0].getName() + " ("
							+ event.getAuthor().getAsMention() + ").";
				} else {
					d = "You need to have the Premissions "
							+ String.join(", ",
									Arrays.stream(Arrays.copyOfRange(permissions, 0, permissions.length - 1))
											.map(Permission::getName).collect(Collectors.toList()))
							+ " and " + permissions[permissions.length - 1].getName() + " ("
							+ event.getAuthor().getAsMention() + ").";
				}
				builder.setDescription(d);
				builder.setColor(Color.RED);
				event.getChannel().sendMessage(builder.build()).queue();
			}
		}
	}

	/**
	 * This is the event function you should override.
	 * 
	 * @param eventthe        {@link MessageReceivedEvent} that is fired then
	 *                        somebody called this command
	 * @param guild           the {@link Guild} the command is called from.
	 * @param commandContenta {@link List} of contend strings that are after the
	 *                        {@link CommandListener#getPrefixs()} and the
	 *                        {@link #names} of the event. If you use them you don't
	 *                        must split the contend yourself.
	 * @throws Throwable If there are Throwables in your code the
	 *                   {@link CommandListener} will catch them for you.
	 */
	public abstract void onServerCommand(MessageReceivedEvent event, Guild guild, List<String> commandContent)
			throws Throwable;
}
