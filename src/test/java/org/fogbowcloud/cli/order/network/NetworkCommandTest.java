package org.fogbowcloud.cli.order.network;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;
import org.fogbowcloud.cli.HttpRequestMatcher;
import org.fogbowcloud.cli.HttpUtil;
import org.fogbowcloud.cli.order.OrderCommand;
import org.fogbowcloud.manager.utils.HttpRequestUtil;
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
	public void setUp() throws ClientProtocolException, IOException {
		this.network = new Network(
				"my-providing-member", 
				"my-gateway",
				"my-address",
				"my-allocation"
		);
		this.networkCommand = new NetworkCommand();
		initHttpClient();
	}
	
	@Test
	public void testRunCreateCommand() throws IOException {
		JCommander.newBuilder()
		    .addObject(this.networkCommand)
		    .build()
		    .parse(
		    		OrderCommand.CREATE_COMMAND_KEY, 
		    		OrderCommand.FEDERATION_TOKEN_COMMAND_KEY, this.token,
		    		OrderCommand.URL_COMMAND_KEY, this.url,
		    		Network.PROVIDING_MEMBER_COMMAND_KEY, this.network.getProvidingMember(),
		    		Network.GATEWAY_COMMAND_KEY, this.network.getGateway(),
		    		Network.ADDRESS_COMMAND_KEY, this.network.getAddress(),
		    		Network.ALLOCATION_COMMAND_KEY, this.network.getAllocation()
		    ); 
	
		String computeJson = new Gson().toJson(this.network);
		HttpPost post = new HttpPost(this.url + NetworkCommand.ENDPOINT);
		post.setEntity(new StringEntity(computeJson));
		post.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		post.setHeader(HttpRequestUtil.CONTENT_TYPE_KEY, HttpRequestUtil.JSON_CONTENT_TYPE_KEY);
		HttpRequestMatcher expectedPostRequest = new HttpRequestMatcher(post);

		this.networkCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedPostRequest));
	}
	
	@Test
	public void testRunDeleteCommand() throws ClientProtocolException, IOException {
		JCommander.newBuilder()
				.addObject(this.networkCommand)
				.build()
				.parse(
					OrderCommand.DELETE_COMMAND_KEY,
					OrderCommand.FEDERATION_TOKEN_COMMAND_KEY, this.token,
					OrderCommand.URL_COMMAND_KEY, this.url,
					OrderCommand.ID_COMMAND_KEY, this.id);

		HttpDelete delete = new HttpDelete(this.url + NetworkCommand.ENDPOINT + '/' + this.id);
		delete.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		HttpRequestMatcher expectedPostRequest = new HttpRequestMatcher(delete);

		this.networkCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedPostRequest));
	}
	
	@Test
	public void testRunGetCommand() throws ClientProtocolException, IOException {
		JCommander.newBuilder()
				.addObject(this.networkCommand)
				.build()
				.parse(
					OrderCommand.GET_COMMAND_KEY,
					OrderCommand.FEDERATION_TOKEN_COMMAND_KEY, this.token,
					OrderCommand.URL_COMMAND_KEY, this.url,
					OrderCommand.ID_COMMAND_KEY, this.id);

		HttpGet get = new HttpGet(this.url + NetworkCommand.ENDPOINT + '/' + this.id);
		get.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		HttpRequestMatcher expectedPostRequest = new HttpRequestMatcher(get);

		this.networkCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedPostRequest));
	}
	
	@Test
	public void testRunGetAllCommand() throws ClientProtocolException, IOException {
		JCommander.newBuilder()
				.addObject(this.networkCommand)
				.build()
				.parse(
					OrderCommand.GET_ALL_COMMAND_KEY,
					OrderCommand.FEDERATION_TOKEN_COMMAND_KEY, this.token,
					OrderCommand.URL_COMMAND_KEY, this.url,
					OrderCommand.ID_COMMAND_KEY, this.id);

		HttpGet get = new HttpGet(this.url + NetworkCommand.ENDPOINT );
		get.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		HttpRequestMatcher expectedPostRequest = new HttpRequestMatcher(get);

		this.networkCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedPostRequest));
	}

	private void initHttpClient() throws ClientProtocolException, IOException {
		this.mockHttpClient = Mockito.mock(HttpClient.class);
		HttpResponseFactory factory = new DefaultHttpResponseFactory();
		HttpResponse response = factory.newHttpResponse(
				new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_CREATED, "Return Irrelevant"), null);
		response.setEntity(new StringEntity("{}"));
		Mockito.when(this.mockHttpClient.execute(Mockito.any(HttpPost.class))).thenReturn(response);
		HttpUtil.setHttpClient(this.mockHttpClient);
	}
}