package cloud.fogbow.cli.ms.member;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.HttpClientMocker;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import com.beust.jcommander.JCommander;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.HashMap;


public class MemberCommandTest {
	
	private MemberCommand memberCommand = new MemberCommand();
	private final String url = "my-url";

	private FogbowCliHttpUtil fogbowCliHttpUtil;

	@Before
	public void setUp() throws IOException, FogbowException {
		this.memberCommand = new MemberCommand();
		this.fogbowCliHttpUtil = HttpClientMocker.init();
		memberCommand.setFogbowCliHttpUtil(this.fogbowCliHttpUtil);
	}
	
	@Test
	public void testRunGetAllCommand() throws FogbowException {
		JCommander.newBuilder()
			.addObject(this.memberCommand)
			.build()
			.parse(
					CliCommonParameters.GET_ALL_COMMAND_KEY,
					CliCommonParameters.URL_COMMAND_KEY, this.url);

		String membersEndpoint = this.url + MemberCommand.ENDPOINT;

		this.memberCommand.run();
		Mockito.verify(this.fogbowCliHttpUtil).doGenericRequest(HttpMethod.GET, membersEndpoint, new HashMap<>(), new HashMap<>());
	}
}
