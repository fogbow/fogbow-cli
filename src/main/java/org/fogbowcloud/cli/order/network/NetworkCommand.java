package org.fogbowcloud.cli.order.network;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.order.OrderCommand;
import org.fogbowcloud.manager.api.http.NetworkOrdersController;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;

public class NetworkCommand {

	public static final String NAME = "network";
	private static final String ENDPOINT = '/' + NetworkOrdersController.NETWORK_ENDPOINT;
	
	@ParametersDelegate
	private Network network = new Network();
	
	@ParametersDelegate
	private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.network);
	
	public String run() throws ClientProtocolException, IOException {
		if (this.orderCommand.getIsCreateCommand()) {
			return this.orderCommand.doCreate();
		} else if (this.orderCommand.getIsDeleteCommand()) {
			return this.orderCommand.doDelete();
		} else if (this.orderCommand.getIsGetCommand()) {
			return this.orderCommand.doGet();
		} else if (this.orderCommand.getIsGetAllCommand()) {
			return this.orderCommand.doGetAll();
		}
		throw new ParameterException("command is incomplete");
	}
}
