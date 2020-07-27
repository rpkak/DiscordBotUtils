package de.rpkak.dbu;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.internal.utils.JDALogger;

/**
 * @author rpkak
 */
public class Utils {

	/**
	 * Shows you if the Bot is active or not.
	 */
	public static boolean active = true;

	private static final Logger LOG = JDALogger.getLog(Utils.class);

	private static Thread thread;

	private static final Map<String, Consumer<String>> commands = new HashMap<String, Consumer<String>>();

	public static void addConsoleCommand(String prefix, Consumer<String> action) {
		commands.put(prefix, action);
	}

	static {
		addConsoleCommand("exit", command -> exit());
	}

	/**
	 * Checks if someone writes "exit" in the console.
	 */
	public static void checkConsole() {
		thread = new Thread(() -> {
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
			String line;
			try {
				while (active) {
					if ((line = inputReader.readLine()) != null) {
						boolean in = false;
						for (String prefix : commands.keySet()) {
							if (line.startsWith(prefix)) {
								LOG.info("Executing \"" + line + "\"");
								commands.get(prefix).accept(line);
								in = true;
								break;
							}
						}
						if (!in) {
							LOG.info("\"" + line + "\" is no command.");
						}
					}
				}
			} catch (IOException e) {
				exit("Error while reading BufferedInputStreamReader of System.in:", e);
			}
		}, "checkConsoleThread");
		thread.start();
	}

	private static Runnable exExit = () -> {
	};

	/**
	 * If you want to close something then the Process stops (like shutting down the
	 * Bot) you can set it here.
	 * 
	 * @param exit A {@link Runnable} that is executed then the Process stops.
	 * @see #exit()
	 * @see #exit(String, Throwable)
	 */
	public static void setExit(Runnable exit) {
		exExit = exit;
	}

	/**
	 * Stops the Process. Is executed in {@link #checkConsole()}.
	 * 
	 * @see #checkConsole()
	 * @see #exit(String, Throwable)
	 * @see #setExit(Runnable)
	 */
	public static void exit() {
		LOG.info("shutting down ...");

		try {
			active = false;
			exExit.run();
		} catch (Exception e) {
			LOG.error("Exception while shutting down:", e);
		}

		LOG.info("shutted down!");
	}

	/**
	 * Stops the Process with a {@link Throwable} and a Message to search it. For
	 * Example:
	 * 
	 * <pre>
	 * try{
	 *     do something ..
	 * }catch(Throwable t){
	 *     Utils.exit("Error in Method ...", t)
	 * }
	 * </pre>
	 * 
	 * @param msg The Message to find the reason of the {@link Throwable}.
	 * @param t   The {@link Throwable}.
	 */
	public static void exit(String msg, Throwable t) {
		LOG.error(msg, t);
		exit();
	}

	/**
	 * if this is true and in {@link #onMessageReceived(MessageReceivedEvent)} an
	 * {@link PermissionException} is thrown the bot will not shutdown but inform
	 * the Server.
	 */
	public static boolean catchPermissionExceptions = true;

	@FunctionalInterface
	public static interface RunnableWithThrowable {
		void run() throws Throwable;
	}

	public static void tryCatch(RunnableWithThrowable action, MessageChannel channel, User user, String onExit,
			Guild guild) {
		try {
			if (catchPermissionExceptions) {
				try {
					action.run();
				} catch (PermissionException e) {
					EmbedBuilder builder = new EmbedBuilder();
					builder.setAuthor("I can't");
					builder.setDescription(
							"I have not the permissions to do this. You can try\n```\n| permissions\n```");
					builder.setColor(Color.RED);
					try {

						channel.sendMessage(builder.build()).queue();
					} catch (PermissionException e2) {
						EmbedBuilder builder2 = new EmbedBuilder();
						builder2.setAuthor("I can't write");
						builder2.setDescription("Please give me the Permission to write on " + guild.getName() + " / "
								+ channel.getName() + ".");
						user.openPrivateChannel().queue(privateChannel -> {
							privateChannel.sendMessage(builder.build()).queue();
							privateChannel.sendMessage(builder2.build()).queue();
						});
						LOG.info("catchInnerPermissionExceptions", e);
					}
					LOG.info("catchPermissionExceptions", e);
				}
			} else {
				action.run();
			}
		} catch (Throwable e) {
			exit(onExit, e);
		}
	}

}
