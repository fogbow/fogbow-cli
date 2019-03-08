package cloud.fogbow.cli.ras.order.network;

import java.io.IOException;

import cloud.fogbow.cli.constants.CliCommonParameters;
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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;

public class NetworkCommandTest {
	private Network network;
	private NetworkCommand networkCommand = new NetworkCommand();
	private HttpClient mockHttpClient;
	
	private final String url = "my-url";
	private final String token = "my-token";
	private final String id = "my-id";
	
	@Before
	public void setUp() throws FogbowCLIException, IOException {
		this.network = new Network(
				"my-provider",
				"my-gateway",
				"my-address",
				"my-allocation",
				"network-name"
		);
		this.networkCommand = new NetworkCommand();
		initHttpClient();
	}
	
	@Test
	public void testRunCreateCommand() throws FogbowCLIException, IOException {
		JCommander.newBuilder()
		    .addObject(this.networkCommand)
		    .build()
		    .parse(
		    		OrderCommand.CREATE_COMMAND_KEY, 
		    		CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
		    		CliCommonParameters.URL_COMMAND_KEY, this.url,
		    		Network.PROVIDER_COMMAND_KEY, this.network.getProvider(),
		    		Network.GATEWAY_COMMAND_KEY, this.network.getGateway(),
		    		Network.ADDRESS_COMMAND_KEY, this.network.getCidr(),
		    		Network.ALLOCATION_COMMAND_KEY, this.network.getAllocation(),
					Network.NAME_COMMAND_KEY, this.network.getName()
		    ); 
	
		String computeJson = new Gson().toJson(this.network);
		HttpPost post = new HttpPost(this.url + NetworkCommand.ENDPOINT);
		post.setEntity(new StringEntity(computeJson));
		post.setHeader(HttpUtil.SYSTEM_USER_TOKEN_HEADER_KEY, token);
		post.setHeader(HttpUtil.CONTENT_TYPE_KEY, HttpUtil.JSON_CONTENT_TYPE_KEY);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(post);

		this.networkCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
	}
	
	@Test
	public void testRunDeleteCommand() throws FogbowCLIException, IOException {
		JCommander.newBuilder()
				.addObject(this.networkCommand)
				.build()
				.parse(
					OrderCommand.DELETE_COMMAND_KEY,
					CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
					CliCommonParameters.URL_COMMAND_KEY, this.url,
					CliCommonParameters.ID_COMMAND_KEY, this.id);

		HttpDelete delete = new HttpDelete(this.url + NetworkCommand.ENDPOINT + '/' + this.id);
		delete.setHeader(HttpUtil.SYSTEM_USER_TOKEN_HEADER_KEY, token);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(delete);

		this.networkCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
	}
	
	@Test
	public void testRunGetCommand() throws FogbowCLIException, IOException {
		JCommander.newBuilder()
				.addObject(this.networkCommand)
				.build()
				.parse(
					CliCommonParameters.GET_COMMAND_KEY,
					CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
					CliCommonParameters.URL_COMMAND_KEY, this.url,
					CliCommonParameters.ID_COMMAND_KEY, this.id);

		HttpGet get = new HttpGet(this.url + NetworkCommand.ENDPOINT + '/' + this.id);
		get.setHeader(HttpUtil.SYSTEM_USER_TOKEN_HEADER_KEY, token);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(get);

		this.networkCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
	}

	@Test
	public void testRunGetAllCommand() throws FogbowCLIException, IOException {
		JCommander.newBuilder()
				.addObject(this.networkCommand)
				.build()
				.parse(
						CliCommonParameters.GET_ALL_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id);

		HttpGet get = new HttpGet(this.url + NetworkCommand.ENDPOINT + "/" + OrderCommand.STATUS_ENDPOINT_KEY);
		get.setHeader(HttpUtil.SYSTEM_USER_TOKEN_HEADER_KEY, token);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(get);

		this.networkCommand.run();

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
