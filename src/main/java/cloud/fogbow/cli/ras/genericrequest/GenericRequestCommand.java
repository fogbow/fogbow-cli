package cloud.fogbow.cli.ras.genericrequest;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.HttpCliConstants;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.utils.CommandUtil;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.GsonHolder;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import org.apache.http.client.methods.RequestBuilder;

import java.io.IOException;
import java.util.HashMap;

public class GenericRequestCommand {
    public static final String NAME = "generic-request";
    private static final String ENDPOINT = "/genericRequest";

    @Parameter(names = CliCommonParameters.MEMBER_ID_COMMAND_KEY, description = Documentation.CommonParameters.MEMBER_ID, required = true)
    private String memberId = null;

    @Parameter(names = CliCommonParameters.CLOUD_NAME_COMMAND_KEY, description = Documentation.CommonParameters.CLOUD_NAME, required = true)
    private String cloudName = null;

    @ParametersDelegate
    private FogbowCliHttpUtil fogbowCliHttpUtil = new FogbowCliHttpUtil();

    @ParametersDelegate
    private GenericRequest genericRequest = new GenericRequest();

    public String run() throws FogbowException {
        String fullUrl = ENDPOINT + "/" + this.memberId + "/" + this.cloudName;
        HashMap body = genericRequest.getHTTPHashMap();
        String httpResponse = fogbowCliHttpUtil.doGenericAuthenticatedRequest(HttpMethod.POST, fullUrl, body);

        return httpResponse;
    }

    private HttpMethod getHetHttpMethod(String method){
        switch(method.toUpperCase()){
            case "GET":
                return HttpMethod.GET;
            case "DELlTE":
                return HttpMethod.DELETE;
            case "POST":
                return HttpMethod.POST;
        }
        throw new IllegalArgumentException(/**TODO*/);
    }
}

