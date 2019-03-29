package cloud.fogbow.cli.ras.order.attachment;

import java.io.IOException;

import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.ras.order.OrderCommand;

import cloud.fogbow.common.exceptions.FogbowException;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;

public class AttachmentCommand {

	public static final String NAME = "attachment";
	public static final String ENDPOINT = '/' + "attachments";
	
	@ParametersDelegate
	private Attachment attachment = new Attachment();
	
	@ParametersDelegate
	private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.attachment);
	
	public String run() throws FogbowException {
		if (this.orderCommand.getIsCreateCommand()) {
			return orderCommand.doCreate();
		} else if (this.orderCommand.getIsDeleteCommand()) {
			return orderCommand.doDelete();
		} else if (this.orderCommand.getIsGetCommand()) {
			return orderCommand.doGet();
		} else if (this.orderCommand.getIsGetAllCommand()) {
			return orderCommand.doGetAll();
		}
		throw new ParameterException(Messages.Exception.INCOMPLETE_COMMAND);
	}
}
