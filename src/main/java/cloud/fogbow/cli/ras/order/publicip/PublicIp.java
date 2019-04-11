package cloud.fogbow.cli.ras.order.publicip;

import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.constants.HttpCommonParameters;
import cloud.fogbow.cli.ras.FogbowCliResource;
import com.beust.jcommander.Parameter;

import java.util.HashMap;

public class PublicIp implements FogbowCliResource {

    @Parameter(names = CliCommonParameters.COMPUTE_ID_COMMAND_KEY,
            description = Documentation.CommonParameters.COMPUTE_ID_PARAMETER, required = true)
    public String computeId = null;

    @Parameter(names = {CliCommonParameters.PROVIDER_COMMAND_KEY},
            description = Documentation.CommonParameters.PROVIDER)
    private String provider = null;

    public PublicIp(String provider, String computeOrderId) {
        super();
        this.provider = provider;
        this.computeId = computeOrderId;
    }

    public PublicIp() {
    }

    public String getProvider() {
        return provider;
    }

    public String getComputeId() {
        return computeId;
    }

    @Override
    public HashMap getHttpHashMap() {
        HashMap body = new HashMap();

        body.put(HttpCommonParameters.PROVIDER, this.provider);
        body.put(HttpCommonParameters.COMPUTE_ID, this.computeId);

        return body;
    }
}
