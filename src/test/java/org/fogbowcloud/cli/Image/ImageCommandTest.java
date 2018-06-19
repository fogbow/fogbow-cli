package org.fogbowcloud.cli.Image;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;
import org.fogbowcloud.cli.HttpRequestMatcher;
import org.fogbowcloud.cli.HttpUtil;
import org.fogbowcloud.cli.image.ImageCommand;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.beust.jcommander.JCommander;

public class ImageCommandTest {
	private ImageCommand imageCommand = new ImageCommand();
	private HttpClient mockHttpClient;
	
	private final String url = "my-url";
	private final String token = "my-token";
	private final String id = "my-id";
	private final String memberId = "memberId";
	
	@Before
	public void setUp() throws ClientProtocolException, IOException {
		this.imageCommand = new ImageCommand();
		initHttpClient();
	}
	
	@Test
	public void testRunGetCommand() throws ClientProtocolException, IOException {
		JCommander.newBuilder()
				.addObject(this.imageCommand)
				.build()
				.parse(
					ImageCommand.GET_COMMAND_KEY,
					ImageCommand.FEDERATION_TOKEN_COMMAND_KEY, this.token,
					ImageCommand.URL_COMMAND_KEY, this.url,
					ImageCommand.ID_COMMAND_KEY, this.id,
					ImageCommand.MEMBER_ID_COMMAND_KEY, this.memberId);

		HttpGet get = new HttpGet(this.url + ImageCommand.ENDPOINT + '/' + this.id);
		get.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		get.setHeader(ImageCommand.MEMBER_ID_HEADER_KEY, this.memberId);
		HttpRequestMatcher expectedPostRequest = new HttpRequestMatcher(get);

		this.imageCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedPostRequest));
	}
	
	@Test
	public void testRunGetAllCommand() throws ClientProtocolException, IOException {
		JCommander.newBuilder()
				.addObject(this.imageCommand)
				.build()
				.parse(
						ImageCommand.GET_ALL_COMMAND_KEY,
						ImageCommand.FEDERATION_TOKEN_COMMAND_KEY, this.token,
						ImageCommand.URL_COMMAND_KEY, this.url,
						ImageCommand.MEMBER_ID_COMMAND_KEY, this.memberId);

		HttpGet get = new HttpGet(this.url + ImageCommand.ENDPOINT);
		get.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		get.setHeader(ImageCommand.MEMBER_ID_HEADER_KEY, this.memberId);
		HttpRequestMatcher expectedPostRequest = new HttpRequestMatcher(get);

		this.imageCommand.run();

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
