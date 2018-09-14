package org.fogbowcloud.cli;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

import org.fogbowcloud.cli.authentication.token.CommandToken;
import org.fogbowcloud.cli.image.ImageCommand;
import org.fogbowcloud.cli.member.MemberCommand;
import org.fogbowcloud.cli.order.attachment.AttachmentCommand;
import org.fogbowcloud.cli.order.compute.ComputeCommand;
import org.fogbowcloud.cli.order.fednet.FederatedNetworkCommand;
import org.fogbowcloud.cli.order.network.NetworkCommand;
import org.fogbowcloud.cli.order.volume.VolumeCommand;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Main {

	private CommandToken tokenCommand;
	private ComputeCommand computeCommand;
	private VolumeCommand volumeCommand;
	private NetworkCommand networkCommand;
	private AttachmentCommand attachmentCommand;
	private ImageCommand imageCommand;
	private MemberCommand memberCommand;
	private FederatedNetworkCommand federatedNetworkCommand;
	
	private JCommander jCommander;
	
	private static PrintStream outputStream;			
	private static final String LOG_FILE_NAME = "log.txt";
	
	public static void main(String[] args) throws IOException {
		Main.initDefaultOutput();
		Main main = new Main();

		main.tokenCommand = new CommandToken();
		main.computeCommand = new ComputeCommand();
		main.volumeCommand = new VolumeCommand();
		main.networkCommand = new NetworkCommand();
		main.attachmentCommand = new AttachmentCommand();
		main.imageCommand = new ImageCommand();
		main.memberCommand = new MemberCommand();
		main.federatedNetworkCommand = new FederatedNetworkCommand();
		
		main.jCommander = JCommander.newBuilder()
				.addCommand(CommandToken.NAME, main.tokenCommand)
				.addCommand(ComputeCommand.NAME, main.computeCommand)
				.addCommand(VolumeCommand.NAME, main.volumeCommand)
				.addCommand(NetworkCommand.NAME, main.networkCommand)
				.addCommand(AttachmentCommand.NAME, main.attachmentCommand)
				.addCommand(ImageCommand.NAME, main.imageCommand)
				.addCommand(MemberCommand.NAME, main.memberCommand)
				.addCommand(FederatedNetworkCommand.NAME, main.federatedNetworkCommand)
				.build();
		try {
			main.jCommander.parse(args);
			main.run();
		} catch (ParameterException e) {
			Main.printToConsole(e);
			
			StringBuilder out = new StringBuilder();
			main.jCommander.usage(out);
			Main.printToConsole(out);
		}
	}

	private void run() {
		try {
			String output = null;
			
			if (this.jCommander.getParsedCommand() == null) {
				throw new ParameterException("command is empty");
			} else {
				switch (this.jCommander.getParsedCommand()) {
				case CommandToken.NAME:
					output = this.tokenCommand.run();
					break;
				case ComputeCommand.NAME:
					output = this.computeCommand.run();
					break;
				case VolumeCommand.NAME:
					output = this.volumeCommand.run();
					break;
				case NetworkCommand.NAME:
					output = this.networkCommand.run();
					break;
				case AttachmentCommand.NAME:
					output = this.attachmentCommand.run();
					break;
				case ImageCommand.NAME:
					output = this.imageCommand.run();
					break;
				case MemberCommand.NAME:
					output = this.memberCommand.run();
					break;
				case FederatedNetworkCommand.NAME:
					output = this.federatedNetworkCommand.run();
					break;
				}
			}
			
			Main.printToConsole(output);
		} catch (IOException e) {
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
