package org.fogbowcloud.cli.order.compute;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.HttpUtil;
import org.fogbowcloud.cli.exceptions.FogbowCLIException;
import org.fogbowcloud.cli.order.OrderCommand;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;

@Parameters(separators = "=", commandDescription = "Compute manipulation")
public class ComputeCommand {

	public static final String NAME = "compute";
	public static final String ENDPOINT = '/' + "computes";
	
	public static final String GET_QUOTA_COMMAND_KEY = "--get-quota";
	@Parameter(names = { GET_QUOTA_COMMAND_KEY }, description = "Get quota")
	private Boolean isGetQuotaCommand = false;
	
	public static final String GET_ALLOCATION_COMMAND_KEY = "--get-allocation";
	@Parameter(names = { GET_ALLOCATION_COMMAND_KEY }, description = "Get alocation")
	private Boolean isGetAllocationCommand = false;
	
	public static final String MEMBER_ID_COMMAND_KEY = "--member-id";
	@Parameter(names = { MEMBER_ID_COMMAND_KEY }, description = "Member's id")
	private String memberId = null;

	@ParametersDelegate
	private ComputeWrappedWithFedNet compute = new ComputeWrappedWithFedNet();
	
	@ParametersDelegate
	private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.compute);
	
	public static final String QUOTA_ENDPOINT_KEY = "/quota/";
	public static final String ALLOCATION_ENDPOINT_KEY = "/allocation/";
	public static final String COMPUTE_ORDER_JSON_KEY = "computeOrder";
	public static final String FEDERATED_NETWORK_ID_JSON_KEY = "federatedNetworkId";
	
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
		} else if (this.orderCommand.getIsGetAllStatusCommand()) {
			return orderCommand.doGetAllStatus();
		}
		throw new ParameterException("command is incomplete");
	}
	
	private String doCreate() throws FogbowCLIException, IOException {
		try {
			this.compute.setPublicKey(readFile(this.compute.getPublicKey()));
		} catch (IOException e) {
			throw new FileNotFoundException("Unable to read public key");
		}
		return this.orderCommand.doCreate();
	}
	
	private String doGetAllocation() throws FogbowCLIException, IOException {
		if (this.memberId == null) {
			throw new ParameterException("No member-id passed as parameter");
		} else {
			String fullUrl = this.orderCommand.getUrl() + ENDPOINT + ALLOCATION_ENDPOINT_KEY + this.memberId;
			HttpResponse httpResponse = HttpUtil.get(fullUrl, this.orderCommand.getFederationToken());
			return HttpUtil.getHttpEntityAsString(httpResponse);
		}
	}

	private String doGetQuota() throws FogbowCLIException, IOException {
		if (this.memberId == null) {
			throw new ParameterException("No member-id passed as parameter");
		} else {
			String fullUrl = this.orderCommand.getUrl() + ENDPOINT + QUOTA_ENDPOINT_KEY + this.memberId;
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
