package org.fogbowcloud.cli;

import org.fogbowcloud.manager.core.plugins.identity.exceptions.TokenCreationException;
import org.fogbowcloud.manager.core.plugins.identity.exceptions.UnauthorizedException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Main {

	private CommandToken commandToken; 
	private JCommander jCommander;

	public Main() {
		this.commandToken = new CommandToken();
	}
	
	public static void main(String[] args) {
		
		Main main = new Main();
		main.commandToken = new CommandToken();
		main.jCommander = JCommander.newBuilder().addCommand("token", main.commandToken).build();

		try {
			main.jCommander.parse(args);
			main.run();
		} catch (ParameterException e) {
			System.out.println(e);
		}

	}

	private void run() {
		try {
			if (jCommander.getParsedCommand().equals("token")) {
				String output = commandToken.run();
				System.out.println(output);
			}
		} catch (ReflectiveOperationException | UnauthorizedException | TokenCreationException e) {
			System.out.println(e);
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
		} catch (ParameterException e) {
			System.out.println(e);
		}
	}
}
