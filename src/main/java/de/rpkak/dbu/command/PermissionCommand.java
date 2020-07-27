package de.rpkak.dbu.command;

import java.util.List;

import de.rpkak.dbu.command.manage.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PermissionCommand extends Command {
	private final EmbedBuilder embedBuilder;

	public PermissionCommand(String name, String[] aliases, String description, EmbedBuilder embedBuilder) {
		super(name, aliases, description);
		this.embedBuilder = embedBuilder;
	}

	public PermissionCommand(EmbedBuilder embedBuilder) {
		super("Permissions", new String[] { "p", "permissions", "perm" },
				"Shows you what permissions the bot needs\n```\n| permissions\n| perm\n| p\n```");
		this.embedBuilder = embedBuilder;
	}

	@Override
	public void onCommand(MessageReceivedEvent event, List<String> commandContent) throws Throwable {
		event.getChannel().sendMessage(embedBuilder.build()).queue();
	}

}
