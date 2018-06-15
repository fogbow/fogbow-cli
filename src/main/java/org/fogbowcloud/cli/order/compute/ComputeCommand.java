package org.fogbowcloud.cli.order.compute;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.HttpUtil;
import org.fogbowcloud.cli.order.OrderCommand;
import org.fogbowcloud.manager.api.http.ComputeOrdersController;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;

@Parameters(separators = "=", commandDescription = "Compute manipulation")
public class ComputeCommand {

	public static final String NAME = "compute";
	public static final String ENDPOINT = '/' + ComputeOrdersController.COMPUTE_ENDPOINT;
	
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
	private Compute compute = new Compute();
	
	@ParametersDelegate
	private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.compute);
	
	public String run() throws ClientProtocolException, IOException {
		if (orderCommand.getIsCreateCommand()) {
			return orderCommand.doCreate();
		} else if (orderCommand.getIsDeleteCommand()) {
			return orderCommand.doDelete();
		} else if (orderCommand.getIsGetCommand()) {
			return orderCommand.doGet();
		} else if (orderCommand.getIsGetAllCommand()) {
			return orderCommand.doGetAll();
		} else if (this.isGetQuotaCommand) {
			return doGetQuota();
		} else if (this.isGetAllocationCommand) {
			return doGetAllocation();
		} 
		throw new ParameterException("command is incomplete");
	}
	
	private String doGetAllocation() throws ClientProtocolException, IOException {
		String fullUrl = this.orderCommand.getUrl() + ENDPOINT + "/allocation/" + this.memberId;
		HttpResponse httpResponse = HttpUtil.get(fullUrl, this.orderCommand.getFederationToken());
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}

	private String doGetQuota() throws ClientProtocolException, IOException {
		String fullUrl = this.orderCommand.getUrl() + ENDPOINT + "/quota/" + this.memberId;
		HttpResponse httpResponse = HttpUtil.get(fullUrl, this.orderCommand.getFederationToken());
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}
}
