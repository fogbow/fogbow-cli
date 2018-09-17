	package org.fogbowcloud.cli.order.volume;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.exceptions.FogbowCLIException;
import org.fogbowcloud.cli.order.OrderCommand;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;

public class VolumeCommand {
	
	public static final String NAME = "volume";
	public static final String ENDPOINT = '/' + "volumes";
	
	@ParametersDelegate
	private Volume volume = new Volume();
	
	@ParametersDelegate
	private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.volume);
	
	public String run() throws FogbowCLIException, IOException {
		if (orderCommand.getIsCreateCommand()) {
			return orderCommand.doCreate();
		} else if (orderCommand.getIsDeleteCommand()) {
			return orderCommand.doDelete();
		} else if (orderCommand.getIsGetCommand()) {
			return orderCommand.doGet();
		} else if (orderCommand.getIsGetAllCommand()) {
			return orderCommand.doGetAll();
		} else if (this.orderCommand.getIsGetAllStatusCommand()) {
			return orderCommand.doGetAllStatus();
		}
		throw new ParameterException("command is incomplete");
	}
}
