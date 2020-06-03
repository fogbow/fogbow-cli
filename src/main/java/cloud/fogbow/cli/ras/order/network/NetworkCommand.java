package cloud.fogbow.cli.ras.order.network;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.ras.order.OrderCommand;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;
import com.google.common.annotations.VisibleForTesting;

public class NetworkCommand {

	public static final String NAME = "network";
	public static final String ENDPOINT = "networks";
	public static final String GET_ALLOCATION_COMMAND_KEY = "--get-allocation";
	public static final String ALLOCATION_ENDPOINT_KEY = "/allocation/";

	@ParametersDelegate
	private Network network = new Network();
	
	@ParametersDelegate
	private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.network);

	@Parameter(names = GET_ALLOCATION_COMMAND_KEY, description = Documentation.Allocation.GET)
	private Boolean isGetAllocationCommand = false;

	public String run() throws FogbowException {
		if (this.orderCommand.getIsCreateCommand()) {
			return this.orderCommand.doCreate();
		} else if (this.orderCommand.getIsDeleteCommand()) {
			return this.orderCommand.doDelete();
		} else if (this.orderCommand.getIsGetCommand()) {
			return this.orderCommand.doGet();
		} else if (this.orderCommand.getIsGetAllCommand()) {
			return this.orderCommand.doGetAll();
		} else if (this.isGetAllocationCommand) {
			return this.doGetAllocation();
		}
		throw new ParameterException(Messages.Exception.INCOMPLETE_COMMAND);
	}

	@VisibleForTesting
	String doGetAllocation() throws FogbowException {
		if (this.orderCommand.getMemberId() == null || this.orderCommand.getCloudName() == null) {
			throw new ParameterException(Messages.Exception.NO_MEMBER_ID_OR_CLOUD_NAME);
		} else {
			String fullPath = ENDPOINT + ALLOCATION_ENDPOINT_KEY + this.orderCommand.getMemberId() +
					"/" + this.orderCommand.getCloudName();
			FogbowCliHttpUtil fogbowCliHttpUtil = orderCommand.getFogbowCliHttpUtil();
			return fogbowCliHttpUtil.doGenericAuthenticatedRequest(HttpMethod.GET, fullPath);

		}
	}

	public void setFogbowCliHttpUtil(FogbowCliHttpUtil fogbowCliHttpUtil) {
		orderCommand.setFogbowCliHttpUtil(fogbowCliHttpUtil);
	}

	public FogbowCliHttpUtil getFogbowCliHttpUtil() {
		return orderCommand.getFogbowCliHttpUtil();
	}
}
