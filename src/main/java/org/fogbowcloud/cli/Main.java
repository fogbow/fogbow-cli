package org.fogbowcloud.cli;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.fogbowcloud.cli.authentication.token.CommandToken;
import org.fogbowcloud.cli.exceptions.FogbowCLIException;
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

	public static final String FOGBOW_CLI_PREFIX = "fogbow-cli: ";
	public static final String LINE_BREAK = System.getProperty("line.separator");

	public static final String USAGE_DEFAULT_MESSAGE = "Usage: fogbow-cli [command] [command options]" + LINE_BREAK +
			LINE_BREAK +
			"Available commands:" + LINE_BREAK +
			"  * token" + LINE_BREAK +
			"  * compute" + LINE_BREAK +
			"  * volume" + LINE_BREAK +
			"  * network" + LINE_BREAK +
			"  * attachment" + LINE_BREAK +
			"  * image" + LINE_BREAK +
			"  * member" + LINE_BREAK +
			"  * federated-network" + LINE_BREAK +
			LINE_BREAK +
			LINE_BREAK +
			"See https://github.com/fogbow/fogbow-cli/blob/master/README.md for more details.";

	public static final String LOG_FILE_NAME = "log.txt";

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

		StringBuilder a = new StringBuilder();
		main.jCommander.usage(a);
		String b = a.toString();
		try {
			main.jCommander.parse(args);
			String output = main.run();
			Main.printToConsole(output);
		} catch (FogbowCLIException e) {
			Main.printToConsole(e);
			Main.printToConsole(e.getMessage());
			Main.printToConsole(e.getCause());
		} catch (ParameterException e) {
			StringBuilder errorMessage = new StringBuilder();
			errorMessage.append(FOGBOW_CLI_PREFIX + e.getMessage());
			errorMessage.append(LINE_BREAK);
			errorMessage.append(LINE_BREAK);
			errorMessage.append(USAGE_DEFAULT_MESSAGE);

			Main.printToConsole(errorMessage);
		}
	}

	private String run() throws IOException, FogbowCLIException {
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
		return output;
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
