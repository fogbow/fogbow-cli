package cloud.fogbow.cli.ras.order.network;

import java.io.IOException;

import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.ras.order.OrderCommand;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;

public class NetworkCommand {

	public static final String NAME = "network";
	public static final String ENDPOINT = '/' + "networks";
	
	@ParametersDelegate
	private Network network = new Network();
	
	@ParametersDelegate
	private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.network);
	
	public String run() throws FogbowCLIException, IOException {
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
}
