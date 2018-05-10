package org.fogbowcloud.cli;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.fogbowcloud.manager.core.plugins.identity.exceptions.TokenCreationException;
import org.fogbowcloud.manager.core.plugins.identity.exceptions.UnauthorizedException;

import com.beust.jcommander.JCommander;

public class Main {

	public static void main(String[] args) {

		configureLog4j();

		CommandToken commandToken = new CommandToken();

		JCommander jCommander = JCommander.newBuilder().addCommand("token", commandToken).build();
		jCommander.parse(args);

		if (jCommander.getParsedCommand().equals("token")) {
			try {
				String output = commandToken.run();
				System.out.println(output);
			} catch (ReflectiveOperationException | UnauthorizedException | TokenCreationException e) {
				System.out.println(e);
				System.out.println(e.getMessage());
				System.out.println(e.getCause());
			}
		}

	}

	private static void configureLog4j() {
		ConsoleAppender console = new ConsoleAppender();
		console.setThreshold(Level.OFF);
		console.activateOptions();
		Logger.getRootLogger().addAppender(console);
	}
}
