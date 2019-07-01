package cloud.fogbow.cli.ras.order.publicip;

import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.ras.order.OrderCommand;
import cloud.fogbow.common.exceptions.FogbowException;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;

public class PublicIpCommand {

    public static final String NAME = "public-ip";
    public static final String ENDPOINT = '/' + "publicIps";

    @ParametersDelegate
    private PublicIp publicIp = new PublicIp();

    @ParametersDelegate
    private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.publicIp);

    public String run() throws FogbowException {
        if (this.orderCommand.getIsCreateCommand()) {
            if (publicIp.getComputeId() == null) {
                throw new ParameterException(String.format(Messages.Exception.INCOMPLETE_COMMAND_S, CliCommonParameters.COMPUTE_ID_COMMAND_KEY));
            }
            return orderCommand.doCreate();
        } else if (this.orderCommand.getIsDeleteCommand()) {
            return orderCommand.doDelete();
        } else if (this.orderCommand.getIsGetAllCommand()) {
            return orderCommand.doGetAll();
        }
        throw new ParameterException(Messages.Exception.INCOMPLETE_COMMAND);
    }

}
