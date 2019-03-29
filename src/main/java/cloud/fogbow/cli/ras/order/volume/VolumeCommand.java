	package cloud.fogbow.cli.ras.order.volume;

import java.io.IOException;

import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.ras.order.OrderCommand;
import cloud.fogbow.cli.exceptions.FogbowCLIException;

import cloud.fogbow.common.exceptions.FogbowException;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;

public class VolumeCommand {
	
	public static final String NAME = "volume";
	public static final String ENDPOINT = '/' + "volumes";
	
	@ParametersDelegate
	private Volume volume = new Volume();
	
	@ParametersDelegate
	private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.volume);
	
	public String run() throws FogbowException {
		if (orderCommand.getIsCreateCommand()) {
			return orderCommand.doCreate();
		} else if (orderCommand.getIsDeleteCommand()) {
			return orderCommand.doDelete();
		} else if (orderCommand.getIsGetCommand()) {
			return orderCommand.doGet();
		} else if (orderCommand.getIsGetAllCommand()) {
			return orderCommand.doGetAll();
		}
		throw new ParameterException(Messages.Exception.INCOMPLETE_COMMAND);
	}
}
