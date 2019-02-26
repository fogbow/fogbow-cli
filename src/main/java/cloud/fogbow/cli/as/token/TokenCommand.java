package cloud.fogbow.cli.as.token;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.constants.Messages;

import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.connectivity.HttpRequestClientUtil;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import com.beust.jcommander.*;


@Parameters(separators = "=", commandDescription = Documentation.Token.COMMAND_DESCRIPTION)
public class TokenCommand {
	public static final String NAME = "token";
	public static final String ENDPOINT = '/' + "tokens";
	public static final String CREATE_COMMAND_KEY =  "--create";
	public static final String DYNAMIC_PARAMS_COMMAND_KEY =  "-D";
	public static final String PUBLIC_KEY = "publicKey";
	public static final String CONTENT_TYPE = "Content-Type";

	@Parameter(names = CREATE_COMMAND_KEY, description = Documentation.Token.CREATE_COMMAND, required = true)
	private Boolean isCreate = false;

	@DynamicParameter(names = DYNAMIC_PARAMS_COMMAND_KEY, description = Documentation.Token.DYNAMIC_PARAMS)
	private Map<String, String> credentials = new HashMap<>();

	@Parameter(names = CliCommonParameters.URL_COMMAND_KEY, description = Documentation.CommonParameters.URL, required = true)
	private String url = null;

	public String run() throws IOException, FogbowCLIException {
		if (this.isCreate) {
			return createToken(this.credentials);
		}
		return null;
	}

	private String createToken(Map<String, String> credentials) throws IOException, FogbowCLIException {
		if (credentials == null || credentials.isEmpty()) {
			throw new ParameterException(Messages.Exception.NO_CREDENTIALS_PARAMS);
		}
		if (this.url == null || this.url.isEmpty()) {
			throw new ParameterException(Messages.Exception.NO_FOGBOW_URL_PARAMS);
		}
		if (this.credentials.get(PUBLIC_KEY) == null || this.credentials.get(PUBLIC_KEY).isEmpty()){
			throw new ParameterException((Messages.Exception.NO_PUBLIC_KEY_PROVIDED));
		}

        Map<String, String> headers = new HashMap<String, String>();

		headers.put(PUBLIC_KEY, credentials.get(PUBLIC_KEY));
		headers.put(CONTENT_TYPE, "application/json");

		String fullUrl = this.url + ENDPOINT;
        HttpResponse httpResponse = null;
		try {
			httpResponse = HttpRequestClientUtil.doGenericRequest(HttpMethod.POST, fullUrl, (HashMap<String, String>) headers, (HashMap<String, String>) credentials);
		} catch (FogbowException e) {
			throw new FogbowCLIException(String.format(Messages.Exception.UNABLE_TO_AUTHENTICATE_S, e.getMessage()));
		}

		return  httpResponse.getContent();
//		String credentialsParams = new Gson().toJson(credentials);
//		HttpResponse httpResponse = HttpUtil.post(fullUrl, credentialsParams);
//		return HttpUtil.getHttpEntityAsString(httpResponse);
//        return "";
	}

    public static void main(String[] args) {
        System.out.println("Hello friends");
    }
}
