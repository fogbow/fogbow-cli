package org.fogbowcloud.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

public class Main {
	
	public static void main(String[] args) {
		CommandToken commandToken = new CommandToken();
		JCommander jCommander = JCommander.newBuilder()
				.addCommand("token", commandToken)
				.build();
		jCommander.parse(args);
		System.out.println(commandToken.create);
	}
	
	@Parameters(separators = "=", commandDescription = "Token manipulation") 
	private static class CommandToken {
		
		@Parameter(names = "--create", description = "Create a new token")
		private Boolean create = false; 
	}
	
}

