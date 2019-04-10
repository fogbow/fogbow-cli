package cloud.fogbow.cli.ras.info;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;

import java.util.HashMap;

public class PublicKeyCommand {
    public static final String NAME = "public-key";
    public static final String ENDPOINT = "publicKey";

    @ParametersDelegate
    private FogbowCliHttpUtil fogbowCliHttpUtil = new FogbowCliHttpUtil();

    public String run() throws FogbowException {
        String fullUrl = this.fogbowCliHttpUtil.getUrl() + "/" + ENDPOINT;

        HashMap headers = new HashMap();
        HashMap body = new HashMap();

        HttpResponse httpResponse = fogbowCliHttpUtil.doGenericRequest(HttpMethod.GET, fullUrl, headers, body);

        return httpResponse.getContent();
    }

    public void setFogbowCliHttpUtil(FogbowCliHttpUtil fogbowCliHttpUtil) {
        this.fogbowCliHttpUtil = fogbowCliHttpUtil;
    }
}
