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
import cloud.fogbow.common.util.GsonHolder;
import cloud.fogbow.common.util.connectivity.HttpRequestClient;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import com.beust.jcommander.*;
import com.google.gson.Gson;
import org.apache.log4j.Logger;


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

	@Parameter(names = CREATE_COMMAND_KEY, description = Documentation.Token.CREATE_COMMAND, required = true)
	private Boolean isCreate = false;

	@DynamicParameter(names = DYNAMIC_PARAMS_COMMAND_KEY, description = Documentation.Token.DYNAMIC_PARAMS)
	private Map<String, String> credentials = new HashMap<>();

	@Parameter(names = CliCommonParameters.URL_COMMAND_KEY, description = Documentation.CommonParameters.URL, required = true)
	private String url = null;

	@Parameter(names = PUBLIC_KEY_COMMAND_KEY, description = Documentation.Token.PUBLIC_KEY_PARAMETER)
	private String publicKey = null;

	@Parameter(names = PUBLIC_KEY_PATH_COMMAND_KEY, description = Documentation.Token.PUBLIC_KEY_PATH_PARAMETER)
	private String publicKeyPath = null;

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

        HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(HttpConstants.CONTENT_TYPE_KEY, HttpConstants.JSON_CONTENT_TYPE_KEY);

		String publicKey = CommandUtil.getApplicationPublicKey(this.publicKey, this.publicKeyPath);
		HashMap body = new HashMap<>();

		body.put(CREDENTIALS_JSON_FIELD, credentials);
		body.put(PUBLIC_KEY_JSON_FIELD, publicKey);

		String fullUrl = this.url + ENDPOINT;
        HttpResponse httpResponse = null;

		try {
			httpResponse = HttpRequestClient.doGenericRequest(HttpMethod.POST, fullUrl, headers, body);
		} catch (FogbowException e) {
			throw new FogbowCLIException(String.format(Messages.Exception.UNABLE_TO_AUTHENTICATE_S, e.getMessage()));
		}

		return httpResponse.getContent();
	}
}

