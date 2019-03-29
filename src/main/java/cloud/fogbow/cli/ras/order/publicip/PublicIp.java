package cloud.fogbow.cli.ras.order.publicip;

import cloud.fogbow.cli.ras.FogbowCliResource;
import com.beust.jcommander.Parameter;

import java.util.HashMap;

public class PublicIp implements FogbowCliResource {

    public static final String PROVIDER_COMMAND_KEY =  "--provider";
    @Parameter(names = {PROVIDER_COMMAND_KEY}, description = "Provider")
    private String provider;

    public static final String COMPUTE_ID_COMMAND_KEY =  "--computeId";
    @Parameter(names = {COMPUTE_ID_COMMAND_KEY}, description = "Id of the compute to associate this ip with")
    private String computeOrderId;

    public PublicIp(String provider, String computeOrderId) {
        super();
        this.provider = provider;
        this.computeOrderId = computeOrderId;
    }

    public PublicIp() {
    }

    public String getProvider() {
        return provider;
    }

    public String getComputeOrderId() {
        return computeOrderId;
    }

    @Override
    public HashMap getHTTPHashMap() {
        return null;
    }
}
