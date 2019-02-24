package cloud.fogbow.cli.ras.order.compute;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.fns.compute.ComputeWrappedWithFedNet;
import org.apache.http.HttpResponse;
import cloud.fogbow.cli.HttpUtil;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.ras.order.OrderCommand;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;

@Parameters(separators = "=", commandDescription = "Compute manipulation")
public class ComputeCommand {
	public static final String NAME = "compute";
	public static final String ENDPOINT = '/' + "computes";
	public static final String QUOTA_ENDPOINT_KEY = "/quota/";
	public static final String ALLOCATION_ENDPOINT_KEY = "/allocation/";
	public static final String GET_QUOTA_COMMAND_KEY = "--get-quota";
	public static final String GET_ALLOCATION_COMMAND_KEY = "--get-allocation";

	@Parameter(names = GET_QUOTA_COMMAND_KEY, description = Documentation.Quota.GET)
	private Boolean isGetQuotaCommand = false;
	
	@Parameter(names = GET_ALLOCATION_COMMAND_KEY, description = Documentation.Allocation.GET)
	private Boolean isGetAllocationCommand = false;
	
	@ParametersDelegate
	private ComputeWrappedWithFedNet compute = new ComputeWrappedWithFedNet();
	
	@ParametersDelegate
	private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.compute);

	public String run() throws FogbowCLIException, IOException {
		if (this.orderCommand.getIsCreateCommand()) {
			return doCreate();
		} else if (this.orderCommand.getIsDeleteCommand()) {
			return this.orderCommand.doDelete();
		} else if (this.orderCommand.getIsGetCommand()) {
			return this.orderCommand.doGet();
		} else if (this.orderCommand.getIsGetAllCommand()) {
			return this.orderCommand.doGetAll();
		} else if (this.isGetQuotaCommand) {
			return doGetQuota();
		} else if (this.isGetAllocationCommand) {
			return doGetAllocation();
		}
		throw new ParameterException(Messages.Exception.INCOMPLETE_COMMAND);
	}
	
	private String doCreate() throws FogbowCLIException, IOException {
		try {
			this.compute.setPublicKey(readFile(this.compute.getPublicKey()));
		} catch (IOException e) {
			throw new FileNotFoundException(Messages.Exception.UNABLE_TO_READ_PUBLIC_KEY_FILE);
		}
		return this.orderCommand.doCreate();
	}
	
	private String doGetAllocation() throws FogbowCLIException, IOException {
		if (this.orderCommand.getMemberId() == null || this.orderCommand.getCloudName() == null) {
			throw new ParameterException(Messages.Exception.NO_MEMBER_ID_OR_CLOUD_NAME);
		} else {
			String fullUrl = this.orderCommand.getUrl() + ENDPOINT + ALLOCATION_ENDPOINT_KEY + this.orderCommand.getMemberId() +
					"/" + this.orderCommand.getCloudName();
			HttpResponse httpResponse = HttpUtil.get(fullUrl, this.orderCommand.getFederationToken());
			return HttpUtil.getHttpEntityAsString(httpResponse);
		}
	}

	private String doGetQuota() throws FogbowCLIException, IOException {
		if (this.orderCommand.getMemberId() == null || this.orderCommand.getCloudName() == null) {
			throw new ParameterException(Messages.Exception.NO_MEMBER_ID_OR_CLOUD_NAME);
		} else {
			String fullUrl = this.orderCommand.getUrl() + ENDPOINT + QUOTA_ENDPOINT_KEY + this.orderCommand.getMemberId() +
					"/" + this.orderCommand.getCloudName();
			HttpResponse httpResponse = HttpUtil.get(fullUrl, this.orderCommand.getFederationToken());
			return HttpUtil.getHttpEntityAsString(httpResponse);
		}
	}

	private String readFile(String path) throws IOException {
		if (path == null) return "";
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, StandardCharsets.UTF_8);
	}
}
