package cloud.fogbow.cli.ras.order.compute;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.HttpClientMocker;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.fns.compute.ComputeWrappedWithFedNet;
import cloud.fogbow.cli.ras.order.OrderCommand;
import cloud.fogbow.cli.utils.CommandUtil;
import cloud.fogbow.cli.utils.KeyValueUtil;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import com.beust.jcommander.JCommander;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;

public class ComputeCommandTest {
	
	private Compute compute;
	private ComputeCommand computeCommand = new ComputeCommand();

	private final String url = "my-url";
	private final String token = "my-token";
	private final String id = "my-id";
	private final String memberId = "my-member-id";
	private final String cloudName = "my-cloud";
	private final String requirementsString = "key1=value1,key2=value2";
	private final String TEST_RESOURCES_PATH = "src/test/resource/";


	private Map<String, String> requirements = new HashMap<>();

	private FogbowCliHttpUtil fogbowCliHttpUtil;

	@Before
	public void setUp() throws FogbowException {
		requirements = new KeyValueUtil.KeyValueConverter().convert(requirementsString);
		this.compute = new Compute(
				"my-provider",
				"",
				"my-image-id", 
				"my-vcpu-count",
				"my-memory", 
				"my-disk",
				Arrays.asList(new String[] {TEST_RESOURCES_PATH + "fake-user-data-1:CLOUD_BOOTHOOK", TEST_RESOURCES_PATH + "fake-user-data-2:CLOUD_CONFIG"}),
				Arrays.asList(new String[] {"fake-network-id-1", "fake-network-id-2"}),
				"compute-name",
				requirements
		);
		this.computeCommand = new ComputeCommand();
		this.fogbowCliHttpUtil = HttpClientMocker.init();
		computeCommand.setFogbowCliHttpUtil(fogbowCliHttpUtil);
	}
	
	@Test
	public void testRunCreateCommand() throws FogbowCLIException, IOException, FogbowException {
		JCommander.newBuilder()
		    .addObject(this.computeCommand)
		    .build()
		    .parse(
		    		OrderCommand.CREATE_COMMAND_KEY,
					CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
					CliCommonParameters.URL_COMMAND_KEY, this.url,
					CliCommonParameters.PROVIDER_COMMAND_KEY, this.compute.getProvider(),
		    		Compute.IMAGE_ID_COMMAND_KEY, this.compute.getImageId(),
		    		Compute.VCPU_COMMAND_KEY, this.compute.getvCPU(),
		    		Compute.MEMORY_COMMAND_KEY, this.compute.getMemory(),
		    		Compute.DISC_COMMAND_KEY, this.compute.getDisk(),
		    		Compute.NETWORK_IDS_COMMAND_KEY, separateBy(this.compute.getNetworkIds(), ","),
					Compute.NAME_COMMAND_KEY, this.compute.getName(),
					Compute.USER_DATA_COMMAND_KEY, separateBy(this.compute.getUserData(), ","),
					Compute.REQUIREMENTS_COMMAND_KEY, requirementsString
		    );

		ComputeWrappedWithFedNet computeWrappedWithFedNet = new ComputeWrappedWithFedNet(this.compute);
		HashMap expectedBody = CommandUtil.removeNullEntries(this.compute.getHttpHashMap());
		String path = ComputeCommand.ENDPOINT;

		this.computeCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.POST, path, expectedBody);
	}

	private String separateBy(Collection<String> networkIds, String separator) {
		StringBuilder result = new StringBuilder();
		for (String networkId : networkIds) {
			result.append(networkId);
			result.append(separator);
		}

		if (result.length() > 0) {
			// remove ending space
			result.deleteCharAt(result.length() - 1);
		}

		return result.toString();
	}

	@Test
	public void testRunDeleteCommand() throws FogbowCLIException, IOException, FogbowException {
		JCommander.newBuilder()
				.addObject(this.computeCommand)
				.build()
				.parse(
					OrderCommand.DELETE_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id);

		String path = ComputeCommand.ENDPOINT + '/' + this.id;
		this.computeCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.DELETE, path);
	}

	@Test
	public void testRunGetCommand() throws FogbowCLIException, FogbowException {
		JCommander.newBuilder()
				.addObject(this.computeCommand)
				.build()
				.parse(
						CliCommonParameters.GET_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id);

		String path = ComputeCommand.ENDPOINT + '/' + this.id;

		this.computeCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.GET, path);
	}

	@Test
	public void testRunGetAllStatusCommand() throws FogbowCLIException, IOException, FogbowException {
		JCommander.newBuilder()
				.addObject(this.computeCommand)
				.build()
				.parse(
						CliCommonParameters.GET_ALL_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.ID_COMMAND_KEY, this.id);

		String path = ComputeCommand.ENDPOINT + "/" +  OrderCommand.STATUS_ENDPOINT_KEY;

		this.computeCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.GET, path);
	}

	@Test
	public void testRunGetQuota() throws FogbowCLIException, IOException, FogbowException {
		JCommander.newBuilder()
				.addObject(this.computeCommand)
				.build()
				.parse(
					ComputeCommand.GET_QUOTA_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.CLOUD_NAME_COMMAND_KEY, this.cloudName,
						CliCommonParameters.MEMBER_ID_COMMAND_KEY, this.memberId);

		String path = ComputeCommand.ENDPOINT + ComputeCommand.QUOTA_ENDPOINT_KEY +
				this.memberId + "/" + this.cloudName;

		this.computeCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.GET, path);
	}

	// test case: When calling run method with mocked methods
	// it must verify if it call the right methods
	@Test
	public void testRunGetAllocation() throws FogbowCLIException, IOException, FogbowException {
		JCommander.newBuilder()
				.addObject(this.computeCommand)
				.build()
				.parse(
					ComputeCommand.GET_ALLOCATION_COMMAND_KEY,
						CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
						CliCommonParameters.URL_COMMAND_KEY, this.url,
						CliCommonParameters.CLOUD_NAME_COMMAND_KEY, this.cloudName,
						CliCommonParameters.MEMBER_ID_COMMAND_KEY, this.memberId);

		String path = ComputeCommand.ENDPOINT + ComputeCommand.ALLOCATION_ENDPOINT_KEY
				+ this.memberId + "/" + this.cloudName;

		this.computeCommand.run();

		verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.GET, path);
	}
}
