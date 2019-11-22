package cloud.fogbow.cli.fns.fednet;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.HttpClientMocker;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.ras.order.OrderCommand;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import com.beust.jcommander.JCommander;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.verify;

public class FederatedNetworkCommandTest {
	private FederatedNetwork federatedNetwork;
	private FederatedNetworkCommand federatedNetworkCommand = new FederatedNetworkCommand();

	private final String url = "my-url";
	private final String token = "my-token";
	private final String id = "my-id";

	private final String fakeAllowedMemberOne = "member-one";
	private final String fakeAllowedMemberTwo = "member-two";
	private final String fakeAllowedMemberThree = "member-three";

	private FogbowCliHttpUtil fogbowCliHttpUtil;

	@Before
	public void setUp() throws FogbowException {
		List<String> allowedMembers = Arrays.asList(fakeAllowedMemberOne, fakeAllowedMemberTwo, fakeAllowedMemberThree);
		this.federatedNetwork = new FederatedNetwork(
				"10.150.15.0/28",
				"testeFedNet",
				allowedMembers,
				"vanilla"
		);
		this.federatedNetworkCommand = new FederatedNetworkCommand();
		this.fogbowCliHttpUtil = HttpClientMocker.init();
		federatedNetworkCommand.setFogbowCliHttpUtil(fogbowCliHttpUtil);
	}

	@Test
	public void testRunCreateCommand() throws FogbowException {
		JCommander.newBuilder()
		    .addObject(this.federatedNetworkCommand)
		    .build()
		    .parse(
		    		OrderCommand.CREATE_COMMAND_KEY,
					CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
					CliCommonParameters.URL_COMMAND_KEY, this.url,
		    		FederatedNetwork.CIDR_NOTATION_COMMAND_KEY, this.federatedNetwork.getCidrNotation(),
		    		FederatedNetwork.NAME_COMMAND_KEY, this.federatedNetwork.getName(),
					FederatedNetwork.ALLOWED_MEMBERS_COMMAND_KEY, fakeAllowedMemberOne,
					FederatedNetwork.ALLOWED_MEMBERS_COMMAND_KEY, fakeAllowedMemberTwo,
					FederatedNetwork.ALLOWED_MEMBERS_COMMAND_KEY, fakeAllowedMemberThree
		    );

		this.federatedNetworkCommand.run();

		String path = FederatedNetworkCommand.ENDPOINT;
		HashMap expectedBody = federatedNetwork.getHttpHashMap();
		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.POST, path, expectedBody);
	}

	@Test
	public void testRunDeleteCommand() throws FogbowException {
		JCommander.newBuilder()
				.addObject(this.federatedNetworkCommand)
				.build()
				.parse(
					OrderCommand.DELETE_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id);

		this.federatedNetworkCommand.run();

		String path = FederatedNetworkCommand.ENDPOINT + '/' + this.id;

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.DELETE, path);
	}

	@Test
	public void testRunGetCommand() throws FogbowException {
		JCommander.newBuilder()
				.addObject(this.federatedNetworkCommand)
				.build()
				.parse(
						CliCommonParameters.GET_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id);

		this.federatedNetworkCommand.run();

		String path = FederatedNetworkCommand.ENDPOINT + '/' + this.id;
		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.GET, path);
	}

	@Test
	public void testRunGetStatusCommand() throws FogbowException {
		JCommander.newBuilder()
				.addObject(this.federatedNetworkCommand)
				.build()
				.parse(
						CliCommonParameters.GET_ALL_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id);

		this.federatedNetworkCommand.run();

		String path = FederatedNetworkCommand.ENDPOINT  + "/" + OrderCommand.STATUS_ENDPOINT_KEY;
		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.GET, path);
	}
}
