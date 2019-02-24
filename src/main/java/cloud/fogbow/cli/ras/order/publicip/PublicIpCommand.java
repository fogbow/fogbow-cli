package cloud.fogbow.cli.ras.order.publicip;

import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.ras.order.OrderCommand;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;
import cloud.fogbow.cli.exceptions.FogbowCLIException;

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
        }
        throw new ParameterException(Messages.Exception.INCOMPLETE_COMMAND);
    }

}
