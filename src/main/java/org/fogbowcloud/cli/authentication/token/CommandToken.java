package org.fogbowcloud.cli.authentication.token;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.fogbowcloud.cli.authentication.CommandAuthentication;
import org.fogbowcloud.manager.core.HomeDir;
import org.fogbowcloud.manager.core.exceptions.UnauthenticatedException;
import org.fogbowcloud.manager.core.plugins.behavior.federationidentity.FederationIdentityPlugin;
import org.fogbowcloud.manager.core.plugins.exceptions.TokenValueCreationException;

import java.util.HashMap;
import java.util.Map;

@Parameters(separators = "=", commandDescription = "Token manipulation")
public class CommandToken extends CommandAuthentication {

	public static final String NAME = "token";

	@Parameter(names = { "--create", "-c" }, description = "Create a new token", required = true)
	private Boolean isCreate = false;

	@DynamicParameter(names = "-D", description = "Dynamic parameters")
	private Map<String, String> credentials = new HashMap<>();

	public String run() throws ReflectiveOperationException, UnauthenticatedException, TokenValueCreationException {
		if (this.isCreate) {
			HomeDir.getInstance().setPath(getConfPath());
			return createToken();
		}

		return null;
	}

	private String createToken()
			throws ReflectiveOperationException, UnauthenticatedException, TokenValueCreationException {

		FederationIdentityPlugin identityPlugin = getFederationIdentityPlugin();
		Map<String, String> userCredentials = new HashMap<>();

		for (String key : this.credentials.keySet()) {
			userCredentials.put(key, this.credentials.get(key));
		}

		String accessId = identityPlugin.createFederationTokenValue(userCredentials);
		return accessId;
	}
}
