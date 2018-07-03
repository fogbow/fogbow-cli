package org.fogbowcloud.cli.order.fednet;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.order.OrderCommand;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;

public class FederatedNetworkCommand {
	
	public static final String NAME = "federated-network";
	public static final String ENDPOINT = '/' + "federatedNetworks";
	
	@ParametersDelegate
	private FederatedNetwork federatedNetwork = new FederatedNetwork();
	
	@ParametersDelegate
	private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.federatedNetwork);
	
	public String run() throws ClientProtocolException, IOException {
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
		throw new ParameterException("command is incomplete");
	}
}
