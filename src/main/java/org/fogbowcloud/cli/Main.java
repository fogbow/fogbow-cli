package org.fogbowcloud.cli;

import java.io.IOException;

import org.fogbowcloud.cli.compute.CommandCompute;
import org.fogbowcloud.cli.token.CommandToken;
import org.fogbowcloud.manager.core.plugins.identity.exceptions.TokenCreationException;
import org.fogbowcloud.manager.core.plugins.identity.exceptions.UnauthorizedException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Main {

	private CommandToken commandToken; 
	private CommandCompute commandCompute;
	
	private JCommander jCommander;
	
	public Main() {
		this.commandToken = new CommandToken();
	}
	
	public static void main(String[] args) {
		Main main = new Main();
		main.commandToken = new CommandToken();
		main.commandCompute = new CommandCompute();
		main.jCommander = JCommander.newBuilder()
				.addCommand(CommandToken.NAME, main.commandToken)
				.addCommand(CommandCompute.NAME, main.commandCompute)
				.build();

		try {
			main.jCommander.parse(args);
			main.run();
		} catch (ParameterException e) {
			System.out.println(e);
		}

	}

	private void run() {
		try {
			String output = null;
			switch (jCommander.getParsedCommand()) {
				case CommandToken.NAME:
					output = commandToken.run();
					break;
				case CommandCompute.NAME:
					output = commandCompute.run();
					break;
			}
			System.out.println(output);
		} catch (ReflectiveOperationException | UnauthorizedException | TokenCreationException | IOException e) {
			System.out.println(e);
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
		} catch (ParameterException e) {
			System.out.println(e);
		}
	}
}
