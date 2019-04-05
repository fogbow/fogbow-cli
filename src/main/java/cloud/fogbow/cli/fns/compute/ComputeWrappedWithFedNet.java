package cloud.fogbow.cli.fns.compute;

import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.ras.FogbowCliResource;
import cloud.fogbow.cli.ras.order.compute.Compute;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class ComputeWrappedWithFedNet extends Compute implements FogbowCliResource {
    public static final String FEDERATED_NETWORK_ID_COMMAND_KEY = "--federated-network-id";

    public static final String FEDNET_ID_JSON_KEY = "federatedNetworkId";

    @Parameter(names = { FEDERATED_NETWORK_ID_COMMAND_KEY }, description = Documentation.FederatedNetwork.ID)
    private String federatedNetworkId;

    public ComputeWrappedWithFedNet() {
    }

    public ComputeWrappedWithFedNet(Compute compute) {

    }

    @Override
    public HashMap getHTTPHashMap() {
        HashMap body = new HashMap();

        body.put(FEDNET_ID_JSON_KEY, federatedNetworkId);

        return body;
    }

    public String getFederatedNetworkId() {
        return federatedNetworkId;
    }
}
