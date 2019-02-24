package cloud.fogbow.cli.Image;

import java.io.IOException;

import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
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
import cloud.fogbow.cli.HttpRequestMatcher;
import cloud.fogbow.cli.HttpUtil;
import cloud.fogbow.cli.ras.image.ImageCommand;
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
	private String cloudName = "cloudName";

	@Before
	public void setUp() throws ClientProtocolException, IOException {
		this.imageCommand = new ImageCommand();
		initHttpClient();
	}
	
	@Test
	public void testRunGetCommand() throws IOException, FogbowCLIException {
		JCommander.newBuilder()
				.addObject(this.imageCommand)
				.build()
				.parse(
						CliCommonParameters.GET_COMMAND_KEY,
						CliCommonParameters.FEDERATION_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id,
						CliCommonParameters.MEMBER_ID_COMMAND_KEY, this.memberId,
						CliCommonParameters.CLOUD_NAME_COMMAND_KEY, this.cloudName);

		String expectedUri = this.url + ImageCommand.ENDPOINT + "/" + this.memberId + "/" + this.cloudName + "/" + this.id;
		HttpGet get = new HttpGet(expectedUri);
		get.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(get);

		this.imageCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
	}
	
	@Test
	public void testRunGetAllCommand() throws IOException, FogbowCLIException {
		JCommander.newBuilder()
				.addObject(this.imageCommand)
				.build()
				.parse(
						CliCommonParameters.GET_ALL_COMMAND_KEY,
						CliCommonParameters.FEDERATION_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.MEMBER_ID_COMMAND_KEY, this.memberId,
						CliCommonParameters.CLOUD_NAME_COMMAND_KEY, this.cloudName);

		String expectedUri = this.url + ImageCommand.ENDPOINT + "/" + this.memberId + "/" + this.cloudName;
		HttpGet get = new HttpGet(expectedUri);
		get.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(get);

		this.imageCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
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
