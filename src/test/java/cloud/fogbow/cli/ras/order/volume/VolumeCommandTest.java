package cloud.fogbow.cli.ras.order.volume;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.HttpClientMocker;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.ras.order.OrderCommand;
import cloud.fogbow.cli.utils.CommandUtil;
import cloud.fogbow.cli.utils.KeyValueUtil;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import com.beust.jcommander.JCommander;
import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;

public class VolumeCommandTest {
	private Volume volume;
	private VolumeCommand volumeCommand = new VolumeCommand();
	private HttpClient mockHttpClient;
	
	private final String url = "my-url";
	private final String token = "my-token";
	private final String id = "my-id";
	private final String requirementsString = "key1=value1,key2=value2";
	private Map<String, String> requirements = new HashMap<>();

	private FogbowCliHttpUtil fogbowCliHttpUtil;

	@Before
	public void setUp() throws IOException, FogbowException {
		requirements = new KeyValueUtil.KeyValueConverter().convert(requirementsString);
		this.volume = new Volume(
				"my-provider",
				1024,
				"volume-name",
				requirements
		);
		this.volumeCommand = new VolumeCommand();
		this.fogbowCliHttpUtil = HttpClientMocker.init();
		volumeCommand.setFogbowCliHttpUtil(fogbowCliHttpUtil);
	}
	
	@Test
	public void testRunCreateCommand() throws IOException, FogbowException {
		JCommander.newBuilder()
		    .addObject(this.volumeCommand)
		    .build()
		    .parse(
		    		OrderCommand.CREATE_COMMAND_KEY,
					CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
					CliCommonParameters.URL_COMMAND_KEY, this.url,
					CliCommonParameters.NAME_COMMAND_KEY, this.volume.getName(),
					CliCommonParameters.PROVIDER_COMMAND_KEY, this.volume.getProvider(),
					Volume.VOLUME_SIZE_COMMAND_KEY, Integer.toString(this.volume.getVolumeSize()),
					Volume.REQUIREMENTS, requirementsString
					);
	
		HashMap expectedBody = CommandUtil.removeNullEntries(this.volume.getHTTPHashMap());
		String path = VolumeCommand.ENDPOINT;

		this.volumeCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.POST, path, expectedBody);
	}

	@Test
	public void testRunDeleteCommand() throws IOException, FogbowException {
		JCommander.newBuilder()
				.addObject(this.volumeCommand)
				.build()
				.parse(
					OrderCommand.DELETE_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id);

		String path = VolumeCommand.ENDPOINT + '/' + this.id;

		this.volumeCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.DELETE, path);
	}

	@Test
	public void testRunGetCommand() throws IOException, FogbowException {
		JCommander.newBuilder()
				.addObject(this.volumeCommand)
				.build()
				.parse(
						CliCommonParameters.GET_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id);

		String path = VolumeCommand.ENDPOINT + '/' + this.id;

		this.volumeCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.GET, path);
	}

	@Test
	public void testRunGetAllStatusCommand() throws IOException, FogbowException {
		JCommander.newBuilder()
				.addObject(this.volumeCommand)
				.build()
				.parse(
						CliCommonParameters.GET_ALL_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id);

		String path = VolumeCommand.ENDPOINT + "/" + OrderCommand.STATUS_ENDPOINT_KEY;

		this.volumeCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.GET, path);
	}
}
