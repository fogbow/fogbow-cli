package org.fogbowcloud.cli.authentication.token;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.fogbowcloud.cli.HttpUtil;
import org.fogbowcloud.cli.authentication.CommandAuthentication;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.fogbowcloud.cli.constants.Messages;

@Parameters(separators = "=", commandDescription = "Token manipulation")
public class CommandToken extends CommandAuthentication {

	public static final String NAME = "token";
	public static final String ENDPOINT = '/' + "tokens";

	@Parameter(names = { "--create", "-c" }, description = "Create a new token", required = true)
	private Boolean isCreate = false;

	@DynamicParameter(names = "-D", description = "Dynamic parameters")
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