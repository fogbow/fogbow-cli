package org.fogbowcloud.cli.order.fednet;

import java.io.IOException;

import org.fogbowcloud.cli.exceptions.FogbowCLIException;
import org.fogbowcloud.cli.order.OrderCommand;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;

public class FederatedNetworkCommand {
	
	public static final String NAME = "federated-network";
	public static final String ENDPOINT = '/' + "federatedNetworks";

	public static final String COMMAND_IS_INCOMPLETE_EXCEPTION_MESSAGE = "Command is incomplete";
	public static final String CLOUD_NAME_NOT_ALLOWED_EXCEPTION_MESSAGE = "Cloud name is not allowed for Federated Network";

	@ParametersDelegate
	private FederatedNetwork federatedNetwork = new FederatedNetwork();
	
	@ParametersDelegate
	private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.federatedNetwork);
	
	public String run() throws FogbowCLIException, IOException {
		if (orderCommand.getCloudName() != null && !orderCommand.getCloudName().isEmpty()) {
			throw new ParameterException(CLOUD_NAME_NOT_ALLOWED_EXCEPTION_MESSAGE);
		}

		if (this.orderCommand.getIsCreateCommand()) {
			return this.orderCommand.doCreate();
		} else if (this.orderCommand.getIsDeleteCommand()) {
			return this.orderCommand.doDelete();
		} else if (this.orderCommand.getIsGetCommand()) {
			return this.orderCommand.doGet();
		} else if (this.orderCommand.getIsGetAllCommand()) {
			return this.orderCommand.doGetAll();
		} else if (this.orderCommand.getIsGetAllStatusCommand()) {
			return orderCommand.doGetAllStatus();
		}
		throw new ParameterException(COMMAND_IS_INCOMPLETE_EXCEPTION_MESSAGE);
	}
}
