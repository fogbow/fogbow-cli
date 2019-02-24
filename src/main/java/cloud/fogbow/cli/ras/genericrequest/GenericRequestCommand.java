package cloud.fogbow.cli.ras.genericrequest;

import cloud.fogbow.cli.HttpUtil;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.utils.CommandUtil;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import cloud.fogbow.cli.exceptions.FogbowCLIException;

import java.io.IOException;

public class GenericRequestCommand {
    public static final String NAME = "genericRequest";
    private static final String ENDPOINT = "/genericRequest";

    @Parameter(names = CliCommonParameters.URL_COMMAND_KEY, description = Documentation.CommonParameters.URL)
    private String url;

    @Parameter(names = CliCommonParameters.MEMBER_ID_COMMAND_KEY, description = Documentation.CommonParameters.MEMBER_ID, required = true)
    private String memberId = null;

    @Parameter(names = CliCommonParameters.CLOUD_NAME_COMMAND_KEY, description = Documentation.CommonParameters.CLOUD_NAME, required = true)
    private String cloudName = null;

    @Parameter(names = CliCommonParameters.FEDERATION_TOKEN_COMMAND_KEY, description = Documentation.CommonParameters.FEDERATION_TOKEN)
    private String federationTokenValue = null;

    @Parameter(names = CliCommonParameters.FEDERATION_TOKEN_PATH_COMMAND_KEY, description = Documentation.CommonParameters.FEDERATION_TOKEN_PATH)
    private String federationTokenPath = null;

    @ParametersDelegate
    private GenericRequest genericRequest = new GenericRequest();

    public String run() throws FogbowCLIException, IOException {
        String fullUrl = this.url + "/" + ENDPOINT + "/" + this.memberId + "/" + this.cloudName;
        String federationToken = CommandUtil.getFederationToken(this.federationTokenValue, this.federationTokenPath);
        HttpResponse httpResponse = HttpUtil.post(fullUrl, new Gson().toJson(this.genericRequest), federationToken);
        return HttpUtil.getHttpEntityAsString(httpResponse);
    }
}
