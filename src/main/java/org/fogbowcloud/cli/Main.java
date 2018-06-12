package org.fogbowcloud.cli;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.fogbowcloud.cli.authentication.token.CommandToken;
import org.fogbowcloud.cli.authentication.user.CommandUser;
import org.fogbowcloud.cli.compute.CommandCompute;
import org.fogbowcloud.manager.core.exceptions.UnauthenticatedException;
import org.fogbowcloud.manager.core.plugins.exceptions.TokenValueCreationException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Main {

	private CommandToken commandToken;
	private CommandCompute commandCompute;
	private CommandUser commandUser;

	private JCommander jCommander;
	
	private static final String LOG_FILE_NAME = "log.txt";
	
	public Main() {
		this.commandToken = new CommandToken();
	}

	public static void main(String[] args) throws IOException {
		
		Main.initDefaultOutput();
		
		Main main = new Main();
		
		main.commandToken = new CommandToken();
		main.commandCompute = new CommandCompute();
		main.commandUser = new CommandUser();

		main.jCommander = JCommander.newBuilder()
				.addCommand(CommandToken.NAME, main.commandToken)
				.addCommand(CommandCompute.NAME, main.commandCompute)
				.addCommand(CommandUser.NAME, main.commandUser)
				.build();

		
		main.jCommander.parse(args);
		main.run();
	}

	private void run() {
		
		try(PrintStream outputStream = new PrintStream(new FileOutputStream(FileDescriptor.out))){
			String output = null;
			
			if (this.jCommander.getParsedCommand() == null) {
				throw new ParameterException("command is empty");
			} else {
				switch (this.jCommander.getParsedCommand()) {
				case CommandToken.NAME:
					output = this.commandToken.run();
					break;
				case CommandCompute.NAME:
					output = this.commandCompute.run();
					break;
				case CommandUser.NAME:
					output = this.commandUser.run();
					break;
				}
			}
			
			outputStream.println(output);
		} catch (ReflectiveOperationException | UnauthenticatedException | TokenValueCreationException
				| IOException e) {
			System.out.println(e);
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
		} catch (ParameterException e) {
			System.out.println(e);
			this.jCommander.usage();
		} catch (Exception e) {
			System.out.println(e);
			this.jCommander.usage();
			System.out.println(this.jCommander.getParsedCommand());
		} 
	}
	
	private static void initDefaultOutput() throws IOException {
		System.setOut(new PrintStream(new FileOutputStream(LOG_FILE_NAME)));
	}
}
