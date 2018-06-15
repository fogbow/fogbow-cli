package org.fogbowcloud.cli.order.network;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.order.OrderCommand;
import org.fogbowcloud.manager.api.http.NetworkOrdersController;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;

public class NetworkCommand {

	public static final String NAME = "network";
	public static final String ENDPOINT = '/' + NetworkOrdersController.NETWORK_ENDPOINT;
	
	@ParametersDelegate
	private Network network = new Network();
	
	@ParametersDelegate
	private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.network);
	
	public String run() throws ClientProtocolException, IOException {
		if (orderCommand.getIsCreateCommand()) {
			return orderCommand.doCreate();
		} else if (orderCommand.getIsDeleteCommand()) {
			return orderCommand.doDelete();
		} else if (orderCommand.getIsGetCommand()) {
			return orderCommand.doGet();
		} else if (orderCommand.getIsGetAllCommand()) {
			return orderCommand.doGetAll();
		}
		throw new ParameterException("command is incomplete");
	}
}
