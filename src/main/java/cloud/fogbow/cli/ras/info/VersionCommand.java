package cloud.fogbow.cli.ras.info;

import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.connectivity.HttpRequestClient;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import com.beust.jcommander.Parameter;

import java.util.HashMap;

public class VersionCommand {
    public static final String NAME = "version";
    public static final String ENDPOINT = "version";

    @Parameter(names = CliCommonParameters.URL_COMMAND_KEY, description = Documentation.CommonParameters.URL, required = true)
    private String url = null;

    public String run() throws FogbowException {
        String fullUrl = this.url + "/" + ENDPOINT;

        HashMap headers = new HashMap();
        HashMap body = new HashMap();

        HttpResponse httpResponse = HttpRequestClient.doGenericRequest(HttpMethod.GET, fullUrl, headers, body);

        return httpResponse.getContent();
    }
}
