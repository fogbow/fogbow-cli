package cloud.fogbow.cli.fns.compute;

import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.ras.FogbowCliResource;
import cloud.fogbow.cli.ras.order.compute.Compute;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import com.beust.jcommander.Parameter;

import java.util.HashMap;

public class ComputeWrappedWithFedNet extends Compute implements FogbowCliResource {
    public static final String FEDERATED_NETWORK_ID_COMMAND_KEY = "--federated-network-id";

    public static final String COMPUTE_ORDER_JSON_KEY = "compute";
    public static final String FEDNET_ID_JSON_KEY = "federatedNetworkId";

    @Parameter(names = { FEDERATED_NETWORK_ID_COMMAND_KEY }, description = Documentation.FederatedNetwork.ID)
    private String federatedNetworkId;

    public ComputeWrappedWithFedNet() {
    }

    public ComputeWrappedWithFedNet(Compute compute) {

    }

    @Override
    public HashMap getHTTPHashMap() throws InvalidParameterException {
        HashMap computeOrder = null;

        if(federatedNetworkId != null){
            computeOrder = new HashMap();
            computeOrder.put(FEDNET_ID_JSON_KEY, federatedNetworkId);
            computeOrder.put(COMPUTE_ORDER_JSON_KEY, super.getHTTPHashMap());
        } else {
            computeOrder = super.getHTTPHashMap();
        }

        return computeOrder;
    }
}
