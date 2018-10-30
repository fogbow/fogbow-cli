package org.fogbowcloud.cli.order.compute;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.google.gson.annotations.SerializedName;

public class ComputeWrappedWithFedNet {

    public static final String FEDERATED_NETWORK_ID_COMMAND_KEY = "--federated-network-id";
    @Parameter(names = { FEDERATED_NETWORK_ID_COMMAND_KEY }, description = "Previously created federated network")
    private String federatedNetworkId;

    public static final String COMPUTE_ORDER_ID_JSON_KEY = "computeOrder";
    @SerializedName(COMPUTE_ORDER_ID_JSON_KEY)
    @ParametersDelegate
    private Compute compute = new Compute();

    public ComputeWrappedWithFedNet() {
    }

    public ComputeWrappedWithFedNet(Compute compute) {
        this.compute = compute;
    }

    public String getPublicKey() {
        return compute.getPublicKey();
    }

    public void setPublicKey(String publicKey) {
        compute.setPublicKey(publicKey);
    }

}
