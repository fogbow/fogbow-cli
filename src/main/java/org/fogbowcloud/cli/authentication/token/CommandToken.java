package org.fogbowcloud.cli.authentication.token;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.*;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.fogbowcloud.cli.HttpUtil;

import org.fogbowcloud.cli.constants.Messages;

@Parameters(separators = "=", commandDescription = "Token manipulation")
public class CommandToken {

	public static final String NAME = "token";
	public static final String ENDPOINT = '/' + "tokens";

	public static final String CREATE_COMMAND_KEY =  "--create";
	public static final String CREATE_COMMAND_ABBREVIATION =  "-c";
	@Parameter(names = { CREATE_COMMAND_KEY, CREATE_COMMAND_ABBREVIATION }, description = "Create a new token", required = true)
	private Boolean isCreate = false;

	public static final String DYNAMIC_PARAMS_COMMAND_KEY =  "-D";
	@DynamicParameter(names = DYNAMIC_PARAMS_COMMAND_KEY, description = "Dynamic parameters")
	private Map<String, String> credentials = new HashMap<>();

	public static final String URL_COMMAND_KEY =  "--url";
	@Parameter(names = { URL_COMMAND_KEY }, description = "Url", required = true)
	private String url = null;

	public String run() throws IOException {
		if (this.isCreate) {
			return createToken(this.credentials);
		}

		return null;
	}

	private String createToken(Map<String, String> credentials) throws IOException {
		if (credentials == null || credentials.isEmpty()) {
			throw new ParameterException(Messages.Exception.NO_CREDENTIALS_PARAMS);
		}
		if (this.url == null || this.url.isEmpty()) {
			throw new ParameterException(Messages.Exception.NO_FOGBOW_URL_PARAMS);
		}

		String fullUrl = this.url + ENDPOINT;
		String credentialsParams = new Gson().toJson(credentials);
		HttpResponse httpResponse = HttpUtil.post(fullUrl, credentialsParams);
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}
}