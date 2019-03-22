package cloud.fogbow.cli.as.token;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.constants.Messages;

import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.utils.CommandUtil;
import cloud.fogbow.common.constants.HttpConstants;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.connectivity.HttpRequestClient;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import com.beust.jcommander.*;


@Parameters(separators = "=", commandDescription = Documentation.Token.COMMAND_DESCRIPTION)
public class TokenCommand {
	public static final String CREATE_COMMAND_KEY =  "--create";
	public static final String CREDENTIALS_JSON_FIELD = "credentials";
	public static final String DYNAMIC_PARAMS_COMMAND_KEY =  "-D";
	public static final String ENDPOINT = '/' + "tokens";
	public static final String NAME = "token";
	public static final String PUBLIC_KEY_COMMAND_KEY = "--public-key";
	public static final String PUBLIC_KEY_JSON_FIELD = "publicKey";
	public static final String PUBLIC_KEY_PATH_COMMAND_KEY = "--public-key-path";

	@Parameter(names = CliCommonParameters.URL_COMMAND_KEY, description = Documentation.CommonParameters.URL, required = true)
	private String url = null;

	@Parameter(names = CREATE_COMMAND_KEY, description = Documentation.Token.CREATE_COMMAND, required = true)
	private Boolean isCreate = false;

	@DynamicParameter(names = DYNAMIC_PARAMS_COMMAND_KEY, description = Documentation.Token.DYNAMIC_PARAMS)
	private Map<String, String> credentials = new HashMap<>();

	@Parameter(names = PUBLIC_KEY_COMMAND_KEY, description = Documentation.Token.PUBLIC_KEY_PARAMETER)
	private String publicKey = null;

	@Parameter(names = PUBLIC_KEY_PATH_COMMAND_KEY, description = Documentation.Token.PUBLIC_KEY_PATH_PARAMETER)
	private String publicKeyPath = null;

	public String run() throws IOException, FogbowCLIException {
		System.out.println("KLJkdhvkdfvjklfdhkvjh");
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

        HashMap headers = getHeaders();
		HashMap body = getBody();

		String fullUrl = this.url + ENDPOINT;
        HttpResponse httpResponse = null;

		try {
			httpResponse = HttpRequestClient.doGenericRequest(HttpMethod.POST, fullUrl, headers, body);
		} catch (FogbowException e) {
			throw new FogbowCLIException(String.format(Messages.Exception.UNABLE_TO_AUTHENTICATE_S, e.getMessage()));
		}

		Token token = Token.fromJson(httpResponse.getContent());
		return token.getTokenValue();
	}

	private HashMap getHeaders(){
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(HttpConstants.CONTENT_TYPE_KEY, HttpConstants.JSON_CONTENT_TYPE_KEY);
		return headers;
	}

	private HashMap getBody() throws FogbowCLIException {
		HashMap body = new HashMap<>();
		String publicKey = CommandUtil.getApplicationPublicKey(this.publicKey, this.publicKeyPath);

		body.put(CREDENTIALS_JSON_FIELD, credentials);
		body.put(PUBLIC_KEY_JSON_FIELD, publicKey);
		return body;
	}
}

