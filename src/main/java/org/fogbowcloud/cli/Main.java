package org.fogbowcloud.cli;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.fogbowcloud.cli.authentication.token.CommandCheckToken;
import org.fogbowcloud.cli.authentication.token.CommandToken;
import org.fogbowcloud.cli.authentication.user.CommandUser;
import org.fogbowcloud.cli.image.ImageCommand;
import org.fogbowcloud.cli.member.MemberCommand;
import org.fogbowcloud.cli.order.attachment.AttachmentCommand;
import org.fogbowcloud.cli.order.compute.ComputeCommand;
import org.fogbowcloud.cli.order.network.NetworkCommand;
import org.fogbowcloud.cli.order.volume.VolumeCommand;
import org.fogbowcloud.manager.core.exceptions.FogbowManagerException;
import org.fogbowcloud.manager.core.exceptions.UnexpectedException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Main {

	private CommandToken commandToken;
	private CommandCheckToken commandCheckToken;
	private ComputeCommand commandCompute;
	private CommandUser commandUser;
	private VolumeCommand commandVolume;
	private NetworkCommand commandNetwork;
	private AttachmentCommand commandAttachment;
	private ImageCommand commandImage;
	private MemberCommand memberCommand;
	
	private JCommander jCommander;
	
	private static PrintStream outputStream;			
	private static final String LOG_FILE_NAME = "log.txt";
	
	public static void main(String[] args) throws IOException {
		Main.initDefaultOutput();
		Main main = new Main();

		main.commandToken = new CommandToken();
		main.commandCompute = new ComputeCommand();
		main.commandUser = new CommandUser();
		main.commandVolume = new VolumeCommand();
		main.commandNetwork = new NetworkCommand();
		main.commandAttachment = new AttachmentCommand();
		main.commandImage = new ImageCommand();
		main.memberCommand = new MemberCommand();
		main.commandCheckToken = new CommandCheckToken();

		main.jCommander = JCommander.newBuilder()
				.addCommand(CommandToken.NAME, main.commandToken)
				.addCommand(ComputeCommand.NAME, main.commandCompute)
				.addCommand(CommandUser.NAME, main.commandUser)
				.addCommand(VolumeCommand.NAME, main.commandVolume)
				.addCommand(NetworkCommand.NAME, main.commandNetwork)
				.addCommand(AttachmentCommand.NAME, main.commandAttachment)
				.addCommand(ImageCommand.NAME, main.commandImage)
				.addCommand(MemberCommand.NAME, main.memberCommand)
				.addCommand(CommandCheckToken.NAME, main.commandCheckToken)
				.build();
		try {
			main.jCommander.parse(args);
			main.run();
		} catch (ParameterException | UnexpectedException e) {
			Main.printToConsole(e);
			
			StringBuilder out = new StringBuilder();
			main.jCommander.usage(out);
			Main.printToConsole(out);
		}
	}

	private void run() throws UnexpectedException {
		try {
			String output = null;
			
			if (this.jCommander.getParsedCommand() == null) {
				throw new ParameterException("command is empty");
			} else {
				switch (this.jCommander.getParsedCommand()) {
				case CommandToken.NAME:
					output = this.commandToken.run();
					break;
				case ComputeCommand.NAME:
					output = this.commandCompute.run();
					break;
				case CommandUser.NAME:
					output = this.commandUser.run();
					break;
				case VolumeCommand.NAME:
					output = this.commandVolume.run();
					break;
				case NetworkCommand.NAME:
					output = this.commandNetwork.run();
					break;
				case AttachmentCommand.NAME:
					output = this.commandAttachment.run();
					break;
				case ImageCommand.NAME:
					output = this.commandImage.run();
					break;
				case MemberCommand.NAME:
					output = this.memberCommand.run();
					break;
				case CommandCheckToken.NAME:
					output = this.commandCheckToken.run();
					break;
				}
			}
			
			Main.printToConsole(output);
		} catch (ReflectiveOperationException | FogbowManagerException | IOException e) {
			Main.printToConsole(e);
			Main.printToConsole(e.getMessage());
			Main.printToConsole(e.getCause());
		} 
	}
	
	private static void initDefaultOutput() throws IOException {
		System.setOut(new PrintStream(new FileOutputStream(LOG_FILE_NAME)));
		System.setErr(new PrintStream(new FileOutputStream(LOG_FILE_NAME)));
		Main.outputStream = new PrintStream(new FileOutputStream(FileDescriptor.out));
	}
	
	public static void printToConsole(Object s) {
		Main.outputStream.println(s.toString());
	}
}
