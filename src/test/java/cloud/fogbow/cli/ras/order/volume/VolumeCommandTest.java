package cloud.fogbow.cli.ras.order.volume;

import cloud.fogbow.cli.constants.CliCommonParameters;
import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;
import cloud.fogbow.cli.HttpRequestMatcher;
import cloud.fogbow.cli.HttpUtil;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.ras.order.OrderCommand;
import cloud.fogbow.cli.utils.KeyValueUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VolumeCommandTest {
	private Volume volume;
	private VolumeCommand volumeCommand = new VolumeCommand();
	private HttpClient mockHttpClient;
	
	private final String url = "my-url";
	private final String token = "my-token";
	private final String id = "my-id";
	private final String requirementsString = "key1=value1,key2=value2";
	private Map<String, String> requirements = new HashMap<>();

	@Before
	public void setUp() throws FogbowCLIException, IOException {
		requirements = new KeyValueUtil.KeyValueConverter().convert(requirementsString);
		this.volume = new Volume(
				"my-provider",
				1024,
				"volume-name",
				requirements
		);
		this.volumeCommand = new VolumeCommand();
		initHttpClient();
	}
	
	@Test
	public void testRunCreateCommand() throws FogbowCLIException, IOException {
		JCommander.newBuilder()
		    .addObject(this.volumeCommand)
		    .build()
		    .parse(
		    		OrderCommand.CREATE_COMMAND_KEY,
					CliCommonParameters.FEDERATION_TOKEN_COMMAND_KEY, this.token,
					CliCommonParameters.URL_COMMAND_KEY, this.url,
					Volume.NAME_COMMAND_KEY, this.volume.getName(),
					Volume.PROVIDER_COMMAND_KEY, this.volume.getProvider(),
					Volume.VOLUME_SIZE_COMMAND_KEY, Integer.toString(this.volume.getVolumeSize()),
					Volume.REQUIREMENTS, requirementsString
					);
	
		String volumeJson = new Gson().toJson(this.volume);
		HttpPost post = new HttpPost(this.url + VolumeCommand.ENDPOINT);
		post.setEntity(new StringEntity(volumeJson));
		post.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		post.setHeader(HttpUtil.CONTENT_TYPE_KEY, HttpUtil.JSON_CONTENT_TYPE_KEY);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(post);

		this.volumeCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
	}
	
	@Test
	public void testRunDeleteCommand() throws FogbowCLIException, IOException {
		JCommander.newBuilder()
				.addObject(this.volumeCommand)
				.build()
				.parse(
					OrderCommand.DELETE_COMMAND_KEY,
						CliCommonParameters.FEDERATION_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id);

		HttpDelete delete = new HttpDelete(this.url + VolumeCommand.ENDPOINT + '/' + this.id);
		delete.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(delete);

		this.volumeCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
	}
	
	@Test
	public void testRunGetCommand() throws FogbowCLIException, IOException {
		JCommander.newBuilder()
				.addObject(this.volumeCommand)
				.build()
				.parse(
						CliCommonParameters.GET_COMMAND_KEY,
						CliCommonParameters.FEDERATION_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id);

		HttpGet get = new HttpGet(this.url + VolumeCommand.ENDPOINT + '/' + this.id);
		get.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(get);

		this.volumeCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
	}

	@Test
	public void testRunGetAllStatusCommand() throws FogbowCLIException, IOException {
		JCommander.newBuilder()
				.addObject(this.volumeCommand)
				.build()
				.parse(
						CliCommonParameters.GET_ALL_COMMAND_KEY,
						CliCommonParameters.FEDERATION_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id);

		HttpGet get = new HttpGet(this.url + VolumeCommand.ENDPOINT + "/" + OrderCommand.STATUS_ENDPOINT_KEY);
		get.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(get);

		this.volumeCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
	}

	private void initHttpClient() throws IOException {
		this.mockHttpClient = Mockito.mock(HttpClient.class);
		HttpResponseFactory factory = new DefaultHttpResponseFactory();
		HttpResponse response = factory.newHttpResponse(
				new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_CREATED, "Return Irrelevant"), null);
		response.setEntity(new StringEntity("{}"));
		Mockito.when(this.mockHttpClient.execute(Mockito.any(HttpPost.class))).thenReturn(response);
		HttpUtil.setHttpClient(this.mockHttpClient);
	}
	
}
