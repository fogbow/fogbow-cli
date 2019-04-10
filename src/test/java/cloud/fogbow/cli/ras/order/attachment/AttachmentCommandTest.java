package cloud.fogbow.cli.ras.order.attachment;

import java.io.IOException;
import java.util.HashMap;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.HttpCliConstants;
import cloud.fogbow.cli.HttpClientMocker;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.HttpCommonParameters;
import cloud.fogbow.cli.ras.order.OrderCommand;
import cloud.fogbow.cli.utils.CommandUtil;
import cloud.fogbow.common.constants.HttpConstants;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
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
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;

import static org.mockito.Mockito.verify;

public class AttachmentCommandTest {
	private Attachment attachment;
	private AttachmentCommand attachmentCommand = new AttachmentCommand();

	private final String url = "my-url";
	private final String token = "my-token";
	private final String id = "my-id";

	private FogbowCliHttpUtil fogbowCliHttpUtil;
	
	@Before
	public void setUp() throws IOException, FogbowException {
		this.attachment = new Attachment(
				"my-provider",
				"source",
				"target",
				"device"
		);
		this.attachmentCommand = new AttachmentCommand();
		this.fogbowCliHttpUtil = HttpClientMocker.init();
		attachmentCommand.setFogbowCliHttpUtil(fogbowCliHttpUtil);
	}
	
	@Test
	public void testRunCreateCommand() throws IOException, FogbowException {
		JCommander.newBuilder()
		    .addObject(this.attachmentCommand)
		    .build()
		    .parse(
		    		OrderCommand.CREATE_COMMAND_KEY,
					CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
					CliCommonParameters.URL_COMMAND_KEY, this.url,
					CliCommonParameters.PROVIDER_COMMAND_KEY, attachment.getProvider(),
		    		Attachment.VOLUME_ID_COMMAND_KEY, attachment.getVolumeId(),
		    		Attachment.COMPUTE_ID_COMMAND_KEY, attachment.getComputeId(),
		    		Attachment.DEVICE_COMMAND_KEY, attachment.getDevice()
		    );

		HashMap expectedBody = CommandUtil.removeNullEntries(this.attachment.getHTTPHashMap());
		String path = AttachmentCommand.ENDPOINT;

		this.attachmentCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.POST, path, expectedBody);
	}
	
	@Test
	public void testRunDeleteCommand() throws FogbowCLIException, IOException, FogbowException {
		JCommander.newBuilder()
				.addObject(this.attachmentCommand)
				.build()
				.parse(
					OrderCommand.DELETE_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id);

		String path = AttachmentCommand.ENDPOINT + '/' + this.id;

		this.attachmentCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.DELETE, path);
	}
	
	@Test
	public void testRunGetCommand() throws FogbowCLIException, IOException, FogbowException {
		JCommander.newBuilder()
				.addObject(this.attachmentCommand)
				.build()
				.parse(
						CliCommonParameters.GET_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id);

		String path = AttachmentCommand.ENDPOINT + '/' + this.id;

		this.attachmentCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.GET, path);
	}
	
	@Test
	public void testRunGetStatusCommand() throws FogbowCLIException, IOException, FogbowException {
		JCommander.newBuilder()
				.addObject(this.attachmentCommand)
				.build()
				.parse(
						CliCommonParameters.GET_ALL_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id);

		String path = AttachmentCommand.ENDPOINT + "/" + OrderCommand.STATUS_ENDPOINT_KEY;

		this.attachmentCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.GET, path);
	}
}
