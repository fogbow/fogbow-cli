package cloud.fogbow.cli.fns.fednet;

import java.io.IOException;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.ras.order.OrderCommand;
import cloud.fogbow.cli.exceptions.FogbowCLIException;

import cloud.fogbow.common.exceptions.FogbowException;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;

public class FederatedNetworkCommand {
	
	public static final String NAME = "federated-network";
	public static final String ENDPOINT = '/' + "federatedNetworks";

	@ParametersDelegate
	private FederatedNetwork federatedNetwork = new FederatedNetwork();
	
	@ParametersDelegate
	private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.federatedNetwork);
	
	public String run() throws FogbowException {
		if (orderCommand.getCloudName() != null && !orderCommand.getCloudName().isEmpty()) {
			throw new ParameterException(Messages.Exception.CLOUD_NAME_NOT_ALLOWED);
		}

		if (this.orderCommand.getIsCreateCommand()) {
			return this.orderCommand.doCreate();
		} else if (this.orderCommand.getIsDeleteCommand()) {
			return this.orderCommand.doDelete();
		} else if (this.orderCommand.getIsGetCommand()) {
			return this.orderCommand.doGet();
		} else if (this.orderCommand.getIsGetAllCommand()) {
			return this.orderCommand.doGetAll();
		}
		throw new ParameterException(Messages.Exception.INCOMPLETE_COMMAND);
	}

	public void setFogbowCliHttpUtil(FogbowCliHttpUtil fogbowCliHttpUtil) {
		orderCommand.setFogbowCliHttpUtil(fogbowCliHttpUtil);
	}

	public FogbowCliHttpUtil getFogbowCliHttpUtil() {
		return orderCommand.getFogbowCliHttpUtil();
	}
}
