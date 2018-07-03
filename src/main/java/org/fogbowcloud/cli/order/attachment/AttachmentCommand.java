package org.fogbowcloud.cli.order.attachment;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.order.OrderCommand;
import org.fogbowcloud.manager.api.http.AttachmentOrdersController;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;

public class AttachmentCommand {

	public static final String NAME = "attachment";
	public static final String ENDPOINT = '/' + AttachmentOrdersController.ATTACHMENT_ENDPOINT;
	
	@ParametersDelegate
	private Attachment attachment = new Attachment();
	
	@ParametersDelegate
	private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.attachment);
	
	public String run() throws ClientProtocolException, IOException {
		if (this.orderCommand.getIsCreateCommand()) {
			return orderCommand.doCreate();
		} else if (this.orderCommand.getIsDeleteCommand()) {
			return orderCommand.doDelete();
		} else if (this.orderCommand.getIsGetCommand()) {
			return orderCommand.doGet();
		} else if (this.orderCommand.getIsGetAllCommand()) {
			return orderCommand.doGetAll();
		} else if (this.orderCommand.getIsGetAllStatusCommand()) {
			return orderCommand.doGetAllStatus();
		}
		throw new ParameterException("command is incomplete");
	}
}
