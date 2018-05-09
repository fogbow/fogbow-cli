package org.fogbowcloud.cli;

import java.lang.reflect.InvocationTargetException;

import com.beust.jcommander.JCommander;

public class Main {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		CommandToken commandToken = new CommandToken();

		JCommander jCommander = JCommander.newBuilder().addCommand("token", commandToken).build();
		jCommander.parse(args);

		if (jCommander.getParsedCommand().equals("token")) {
			commandToken.handle();
		}

	}
}
