package cloud.fogbow.cli.ms.member;

import java.io.IOException;

import cloud.fogbow.cli.HttpUtil;
import cloud.fogbow.cli.constants.CliCommonParameters;
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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.beust.jcommander.JCommander;

public class MemberCommandTest {
	
	private MemberCommand memberCommand = new MemberCommand();
	private HttpClient mockHttpClient;
	private final String url = "my-url";
	
	@Before
	public void setUp() throws IOException {
		this.memberCommand = new MemberCommand();
		initHttpClient();
	}
	
	@Test
	public void testRunGetAllCommand() throws IOException {
		JCommander.newBuilder()
			.addObject(this.memberCommand)
			.build()
			.parse(
					CliCommonParameters.GET_ALL_COMMAND_KEY,
					CliCommonParameters.URL_COMMAND_KEY, this.url);
		
		HttpGet get = new HttpGet(this.url + MemberCommand.ENDPOINT);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(get);
		
		this.memberCommand.run();
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
