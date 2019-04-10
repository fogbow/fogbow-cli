package cloud.fogbow.cli;

import cloud.fogbow.cli.as.token.TokenCommand;
import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.fns.fednet.FederatedNetworkCommand;
import cloud.fogbow.cli.ms.member.MemberCommand;
import cloud.fogbow.cli.ras.cloud.CloudsCommand;
import cloud.fogbow.cli.ras.genericrequest.GenericRequestCommand;
import cloud.fogbow.cli.ras.image.ImageCommand;
import cloud.fogbow.cli.ras.info.PublicKeyCommand;
import cloud.fogbow.cli.ras.info.VersionCommand;
import cloud.fogbow.cli.ras.order.attachment.AttachmentCommand;
import cloud.fogbow.cli.ras.order.compute.ComputeCommand;
import cloud.fogbow.cli.ras.order.network.NetworkCommand;
import cloud.fogbow.cli.ras.order.publicip.PublicIpCommand;
import cloud.fogbow.cli.ras.order.volume.VolumeCommand;
import cloud.fogbow.cli.ras.securityrule.SecurityRuleCommand;
import cloud.fogbow.common.exceptions.FogbowException;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Main {
	public static final String FOGBOW_CLI_PREFIX = "fogbow-cli: ";
	public static final String LINE_BREAK = System.getProperty("line.separator");

	public static final String USAGE_DEFAULT_MESSAGE = "Usage: fogbow-cli [command] [command options]" + LINE_BREAK +
			LINE_BREAK +
			"Available commands:" + LINE_BREAK +
			"  * attachment" + LINE_BREAK +
			"  * clouds" + LINE_BREAK +
			"  * compute" + LINE_BREAK +
			"  * federated-network" + LINE_BREAK +
			"  * generic-request" + LINE_BREAK +
			"  * image" + LINE_BREAK +
			"  * member" + LINE_BREAK +
			"  * network" + LINE_BREAK +
			"  * public-key" + LINE_BREAK +
			"  * public-ip" + LINE_BREAK +
			"  * security-rule" + LINE_BREAK +
			"  * token" + LINE_BREAK +
			"  * version" + LINE_BREAK +
			"  * volume" + LINE_BREAK +
			LINE_BREAK +
			LINE_BREAK +
			"See https://github.com/fogbow/fogbow-cli/blob/master/README.md for more details.";

	public static final String LOG_FILE_NAME = "log.txt";

	private AttachmentCommand attachmentCommand;
	private CloudsCommand cloudsCommand;
	private ComputeCommand computeCommand;
	private FederatedNetworkCommand federatedNetworkCommand;
	private GenericRequestCommand genericRequestCommand;
	private ImageCommand imageCommand;
	private MemberCommand memberCommand;
	private NetworkCommand networkCommand;
	private PublicKeyCommand publicKeyCommand;
	private PublicIpCommand publicIpCommand;
	private SecurityRuleCommand securityRuleCommand;
	private TokenCommand tokenCommand;
	private VersionCommand versionCommand;
	private VolumeCommand volumeCommand;

	private JCommander jCommander;

	private static PrintStream outputStream;

	public static void main(String[] args) throws IOException {
		Main.initDefaultOutput();
		Main main = new Main();
		main.attachmentCommand = new AttachmentCommand();
		main.cloudsCommand = new CloudsCommand();
		main.computeCommand = new ComputeCommand();
		main.federatedNetworkCommand = new FederatedNetworkCommand();
		main.genericRequestCommand = new GenericRequestCommand();
		main.imageCommand = new ImageCommand();
		main.memberCommand = new MemberCommand();
		main.networkCommand = new NetworkCommand();
		main.publicKeyCommand = new PublicKeyCommand();
		main.securityRuleCommand = new SecurityRuleCommand();
		main.publicIpCommand = new PublicIpCommand();
		main.tokenCommand = new TokenCommand();
		main.versionCommand = new VersionCommand();
		main.volumeCommand = new VolumeCommand();

		main.jCommander = JCommander.newBuilder()
				.addCommand(AttachmentCommand.NAME, main.attachmentCommand)
				.addCommand(CloudsCommand.NAME, main.cloudsCommand)
				.addCommand(ComputeCommand.NAME, main.computeCommand)
				.addCommand(FederatedNetworkCommand.NAME, main.federatedNetworkCommand)
				.addCommand(GenericRequestCommand.NAME, main.genericRequestCommand)
				.addCommand(ImageCommand.NAME, main.imageCommand)
				.addCommand(MemberCommand.NAME, main.memberCommand)
				.addCommand(NetworkCommand.NAME, main.networkCommand)
				.addCommand(PublicKeyCommand.NAME, main.publicKeyCommand)
				.addCommand(PublicIpCommand.NAME, main.publicIpCommand)
				.addCommand(SecurityRuleCommand.NAME, main.securityRuleCommand)
				.addCommand(TokenCommand.NAME, main.tokenCommand)
				.addCommand(VersionCommand.NAME, main.versionCommand)
				.addCommand(VolumeCommand.NAME, main.volumeCommand)
				.build();

		StringBuilder a = new StringBuilder();
		main.jCommander.usage(a);
		try {
			main.jCommander.parse(args);
			String output = main.run();
			Main.printToConsole(output);
		} catch (FogbowCLIException | FogbowException e) {
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

	private String run() throws IOException, FogbowCLIException, FogbowException {
		String output = null;

		if (this.jCommander.getParsedCommand() == null) {
			throw new ParameterException(Messages.Exception.EMPTY_COMMAND);
		} else {
			switch (this.jCommander.getParsedCommand()) {
				case AttachmentCommand.NAME:
					output = this.attachmentCommand.run();
					break;
				case CloudsCommand.NAME:
					output = this.cloudsCommand.run();
					break;
				case ComputeCommand.NAME:
					output = this.computeCommand.run();
					break;
				case FederatedNetworkCommand.NAME:
					output = this.federatedNetworkCommand.run();
					break;
				case GenericRequestCommand.NAME:
					output = this.genericRequestCommand.run();
					break;
				case ImageCommand.NAME:
					output = this.imageCommand.run();
					break;
				case MemberCommand.NAME:
					output = this.memberCommand.run();
					break;
				case NetworkCommand.NAME:
					output = this.networkCommand.run();
					break;
				case PublicKeyCommand.NAME:
					output = this.publicKeyCommand.run();
					break;
				case PublicIpCommand.NAME:
					output = this.publicIpCommand.run();
					break;
				case SecurityRuleCommand.NAME:
					output = this.securityRuleCommand.run();
					break;
				case TokenCommand.NAME:
					output = this.tokenCommand.run();
					break;
				case VersionCommand.NAME:
					output = this.versionCommand.run();
					break;
				case VolumeCommand.NAME:
					output = this.volumeCommand.run();
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
