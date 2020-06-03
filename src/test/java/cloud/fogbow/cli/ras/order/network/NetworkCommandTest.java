package cloud.fogbow.cli.ras.order.network;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.HttpClientMocker;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.ras.order.OrderCommand;
import cloud.fogbow.cli.ras.order.compute.ComputeCommand;
import cloud.fogbow.cli.utils.CommandUtil;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import com.beust.jcommander.JCommander;
import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.mockito.Mockito.verify;

public class NetworkCommandTest {
	private Network network;
	private NetworkCommand networkCommand = new NetworkCommand();
	private HttpClient mockHttpClient;
	
	private final String url = "my-url";
	private final String token = "my-token";
	private final String id = "my-id";
	private final String memberId = "member-id";
	private final String cloudName = "cloud-name";

	private FogbowCliHttpUtil fogbowCliHttpUtil;
	
	@Before
	public void setUp() throws FogbowException {
		this.network = new Network(
				"my-provider",
				"my-gateway",
				"my-address",
				"my-allocation",
				"network-name"
		);
		this.networkCommand = new NetworkCommand();
		this.fogbowCliHttpUtil = HttpClientMocker.init();
		networkCommand.setFogbowCliHttpUtil(fogbowCliHttpUtil);
	}
	
	@Test
	public void testRunCreateCommand() throws FogbowException {
		JCommander.newBuilder()
		    .addObject(this.networkCommand)
		    .build()
		    .parse(
		    		OrderCommand.CREATE_COMMAND_KEY, 
		    		CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
		    		CliCommonParameters.URL_COMMAND_KEY, this.url,
		    		CliCommonParameters.PROVIDER_COMMAND_KEY, this.network.getProvider(),
		    		Network.GATEWAY_COMMAND_KEY, this.network.getGateway(),
		    		Network.ADDRESS_COMMAND_KEY, this.network.getCidr(),
		    		Network.ALLOCATION_COMMAND_KEY, this.network.getAllocation(),
					Network.NAME_COMMAND_KEY, this.network.getName()
		    );

		HashMap expectedBody = CommandUtil.removeNullEntries(this.network.getHttpHashMap());
		String path = NetworkCommand.ENDPOINT;

		this.networkCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.POST, path, expectedBody);
	}
	
	@Test
	public void testRunDeleteCommand() throws FogbowException {
		JCommander.newBuilder()
				.addObject(this.networkCommand)
				.build()
				.parse(
					OrderCommand.DELETE_COMMAND_KEY,
					CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
					CliCommonParameters.URL_COMMAND_KEY, this.url,
					CliCommonParameters.ID_COMMAND_KEY, this.id);

		String path = NetworkCommand.ENDPOINT + '/' + this.id;

		this.networkCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.DELETE, path);
	}
	
	@Test
	public void testRunGetCommand() throws FogbowException {
		JCommander.newBuilder()
				.addObject(this.networkCommand)
				.build()
				.parse(
					CliCommonParameters.GET_COMMAND_KEY,
					CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
					CliCommonParameters.URL_COMMAND_KEY, this.url,
					CliCommonParameters.ID_COMMAND_KEY, this.id);

		String path = NetworkCommand.ENDPOINT + '/' + this.id;

		this.networkCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.GET, path);
	}

	@Test
	public void testRunGetAllCommand() throws FogbowException {
		JCommander.newBuilder()
				.addObject(this.networkCommand)
				.build()
				.parse(
						CliCommonParameters.GET_ALL_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id);

		String path = NetworkCommand.ENDPOINT + "/" + OrderCommand.STATUS_ENDPOINT_KEY;

		this.networkCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.GET, path);
	}

	// test case: When calling run method with mocked methods
	// it must verify if it call the right methods
	@Test
	public void testRunGetAllocation() throws FogbowCLIException, IOException, FogbowException {
		JCommander.newBuilder()
				.addObject(this.networkCommand)
				.build()
				.parse(
						NetworkCommand.GET_ALLOCATION_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.CLOUD_NAME_COMMAND_KEY, this.cloudName,
						CliCommonParameters.MEMBER_ID_COMMAND_KEY, this.memberId);

		String path = NetworkCommand.ENDPOINT + NetworkCommand.ALLOCATION_ENDPOINT_KEY
				+ this.memberId + "/" + this.cloudName;

		this.networkCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.GET, path);
	}
}
