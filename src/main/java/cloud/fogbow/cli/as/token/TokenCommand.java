package cloud.fogbow.cli.as.token;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cloud.fogbow.cli.HttpUtil;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.constants.Messages;
import com.beust.jcommander.*;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;

@Parameters(separators = "=", commandDescription = Documentation.Token.COMMAND_DESCRIPTION)
public class TokenCommand {
	public static final String NAME = "token";
	public static final String ENDPOINT = '/' + "tokens";
	public static final String CREATE_COMMAND_KEY =  "--create";
	public static final String DYNAMIC_PARAMS_COMMAND_KEY =  "-D";

	@Parameter(names = CREATE_COMMAND_KEY, description = Documentation.Token.CREATE_COMMAND, required = true)
	private Boolean isCreate = false;

	@DynamicParameter(names = DYNAMIC_PARAMS_COMMAND_KEY, description = Documentation.Token.DYNAMIC_PARAMS)
	private Map<String, String> credentials = new HashMap<>();

	@Parameter(names = CliCommonParameters.URL_COMMAND_KEY, description = Documentation.CommonParameters.URL, required = true)
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