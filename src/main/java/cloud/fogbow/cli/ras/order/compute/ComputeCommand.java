package cloud.fogbow.cli.ras.order.compute;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.fns.compute.ComputeWrappedWithFedNet;
import cloud.fogbow.cli.ras.order.OrderCommand;
import cloud.fogbow.cli.utils.CommandUtil;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;

import java.util.HashMap;

@Parameters(separators = "=", commandDescription = "CompunetworkIdste manipulation")
public class ComputeCommand {
    public static final String ALLOCATION_ENDPOINT_KEY = "/allocation/";
    public static final String CLOUD_NAME_KEY = "cloudName";
    public static final String ENDPOINT = "computes";
    public static final String GET_QUOTA_COMMAND_KEY = "--get-quota";
    public static final String GET_ALLOCATION_COMMAND_KEY = "--get-allocation";
    public static final String NAME = "compute";

    public static final String QUOTA_ENDPOINT_KEY = "/quota/";

    @Parameter(names = GET_QUOTA_COMMAND_KEY, description = Documentation.Quota.GET)
    private Boolean isGetQuotaCommand = false;

    @Parameter(names = GET_ALLOCATION_COMMAND_KEY, description = Documentation.Allocation.GET)
    private Boolean isGetAllocationCommand = false;

    @ParametersDelegate
    private ComputeWrappedWithFedNet compute = new ComputeWrappedWithFedNet();

    @ParametersDelegate
    private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.compute);

    public String run() throws FogbowException, FogbowCLIException {
        if (this.orderCommand.getIsCreateCommand()) {
            return doCreate();
        } else if (this.orderCommand.getIsDeleteCommand()) {
            return this.orderCommand.doDelete();
        } else if (this.orderCommand.getIsGetCommand()) {
            return this.orderCommand.doGet();
        } else if (this.orderCommand.getIsGetAllCommand()) {
            return this.orderCommand.doGetAll();
        } else if (this.isGetQuotaCommand) {
            return doGetQuota();
        } else if (this.isGetAllocationCommand) {
            return doGetAllocation();
        }
        throw new ParameterException(Messages.Exception.INCOMPLETE_COMMAND);
    }

    private String doCreate() throws FogbowException {
        HashMap computeOrder = CommandUtil.removeNullEntries(getComputeBody());

        FogbowCliHttpUtil fogbowCliHttpUtil = orderCommand.getFogbowCliHttpUtil();
        String createComputeResponse = fogbowCliHttpUtil.doGenericAuthenticatedRequest(HttpMethod.POST, ENDPOINT, computeOrder);

        return createComputeResponse;
    }

    private HashMap getComputeBody() throws InvalidParameterException {
        HashMap body = compute.getHttpHashMap();
        body.put(CLOUD_NAME_KEY, orderCommand.getCloudName());
        return body;
    }

    private String doGetAllocation() throws FogbowException {
        if (this.orderCommand.getMemberId() == null || this.orderCommand.getCloudName() == null) {
            throw new ParameterException(Messages.Exception.NO_MEMBER_ID_OR_CLOUD_NAME);
        } else {
            String fullPath = ENDPOINT + ALLOCATION_ENDPOINT_KEY + this.orderCommand.getMemberId() +
                    "/" + this.orderCommand.getCloudName();
            FogbowCliHttpUtil fogbowCliHttpUtil = orderCommand.getFogbowCliHttpUtil();
            return fogbowCliHttpUtil.doGenericAuthenticatedRequest(HttpMethod.GET, fullPath);

        }
    }

    private String doGetQuota() throws FogbowException {
        if (this.orderCommand.getMemberId() == null || this.orderCommand.getCloudName() == null) {
            throw new ParameterException(Messages.Exception.NO_MEMBER_ID_OR_CLOUD_NAME);
        } else {
            String fullPath = ENDPOINT + QUOTA_ENDPOINT_KEY + this.orderCommand.getMemberId() +
                    "/" + this.orderCommand.getCloudName();

            FogbowCliHttpUtil fogbowCliHttpUtil = orderCommand.getFogbowCliHttpUtil();
            return fogbowCliHttpUtil.doGenericAuthenticatedRequest(HttpMethod.GET, fullPath);
        }
    }

    public void setFogbowCliHttpUtil(FogbowCliHttpUtil fogbowCliHttpUtil) {
        orderCommand.setFogbowCliHttpUtil(fogbowCliHttpUtil);
    }

    public FogbowCliHttpUtil getFogbowCliHttpUtil() {
        return orderCommand.getFogbowCliHttpUtil();
    }
}


