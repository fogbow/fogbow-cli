package org.fogbowcloud.cli.order.publicip;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;
import org.fogbowcloud.cli.exceptions.FogbowCLIException;
import org.fogbowcloud.cli.order.OrderCommand;

import java.io.IOException;

public class PublicIpCommand {

    public static final String NAME = "public-ip";
    public static final String ENDPOINT = '/' + "publicIps";

    @ParametersDelegate
    private PublicIp publicIp = new PublicIp();

    @ParametersDelegate
    private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.publicIp);

    public String run() throws FogbowCLIException, IOException {
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
