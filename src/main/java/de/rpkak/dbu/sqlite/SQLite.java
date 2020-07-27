package de.rpkak.dbu.sqlite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;

import net.dv8tion.jda.internal.utils.JDALogger;

public class SQLite {

	private static final Logger LOG = JDALogger.getLog(SQLite.class);

	private Connection connection;
	private String path;
	private boolean connected = false;
	private Statement statement;

	/**
	 * Creates a new instance of a {@link SQLite} Connection to a file.
	 * 
	 * @param path the path of the file
	 */
	public SQLite(String path) throws IOException {
		this.path = path;

		File file = new File(path);
		if (!file.exists()) {
			file.createNewFile();
		}
	}

	/**
	 * opens the {@link SQLite} connection
	 */
	public void connect() throws SQLException {
		connection = DriverManager.getConnection("jdbc:sqlite:" + path);
		statement = connection.createStatement();
		LOG.info("Created new SQLite Connection to \"" + path + "\".");
		connected = true;
	}

	/**
	 * closes the {@link SQLite} connection
	 */
	public void disconnect() throws SQLException {
		if (connected) {
			connection.close();
			LOG.info("Closed SQLite Connection to \"" + path + "\".");
			connected = false;
		}
	}

	/**
	 * executes the sql String that returns nothing.
	 * 
	 * @param sql the sql String
	 */
	public void executeWithoutResult(String sql) throws SQLException {
		LOG.info("Executing: '" + sql + "' (no result)");
		statement.execute(sql);
	}

	/**
	 * executes the sql String that returns something.
	 * 
	 * @param sql the sql String
	 * @return a {@link ResultSet} that has the result of the sql execution in it.
	 */
	public ResultSet executeWithResult(String sql) throws SQLException {
		LOG.info("Executing: '" + sql + "'");
		return statement.executeQuery(sql);
	}
}
