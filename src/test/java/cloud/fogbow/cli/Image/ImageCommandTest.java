package cloud.fogbow.cli.Image;

import java.io.IOException;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.HttpCliConstants;
import cloud.fogbow.cli.HttpClientMocker;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.common.constants.HttpConstants;
import cloud.fogbow.common.exceptions.FogbowException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;
import cloud.fogbow.cli.HttpRequestMatcher;
import cloud.fogbow.cli.ras.image.ImageCommand;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.beust.jcommander.JCommander;

public class ImageCommandTest {
	private ImageCommand imageCommand = new ImageCommand();
	
	private final String url = "my-url";
	private final String token = "my-token";
	private final String id = "my-id";
	private final String memberId = "memberId";
	private String cloudName = "cloudName";
	private FogbowCliHttpUtil fogbowCliHttpUtil;

	@Before
	public void setUp() throws IOException, FogbowException {
		this.imageCommand = new ImageCommand();
		this.fogbowCliHttpUtil = HttpClientMocker.init();
		imageCommand.setFogbowCliHttpUtil(this.fogbowCliHttpUtil);
	}
	
	@Test
	public void testRunGetCommand() throws FogbowCLIException, FogbowException {
		JCommander.newBuilder()
				.addObject(this.imageCommand)
				.build()
				.parse(
						CliCommonParameters.GET_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id,
						CliCommonParameters.MEMBER_ID_COMMAND_KEY, this.memberId,
						CliCommonParameters.CLOUD_NAME_COMMAND_KEY, this.cloudName);

		String expectedUri = ImageCommand.ENDPOINT + "/" + this.memberId + "/" + this.cloudName + "/" + this.id;

		this.imageCommand.run();
		Mockito.verify(this.fogbowCliHttpUtil).doAuthenticatedGET(Mockito.eq(expectedUri));
	}
	
	@Test
	public void testRunGetAllCommand() throws FogbowCLIException, FogbowException {
		JCommander.newBuilder()
				.addObject(this.imageCommand)
				.build()
				.parse(
						CliCommonParameters.GET_ALL_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.MEMBER_ID_COMMAND_KEY, this.memberId,
						CliCommonParameters.CLOUD_NAME_COMMAND_KEY, this.cloudName);

		String expectedUri = this.url + ImageCommand.ENDPOINT + "/" + this.memberId + "/" + this.cloudName;

		this.imageCommand.run();

		Mockito.verify(this.fogbowCliHttpUtil).doAuthenticatedGET(Mockito.eq(expectedUri));
	}
}
