package org.fogbowcloud.cli.authentication.token;

import java.util.HashMap;
import java.util.Map;

import org.fogbowcloud.cli.authentication.CommandAuthentication;
import org.fogbowcloud.manager.core.HomeDir;
import org.fogbowcloud.manager.core.exceptions.TokenValueCreationException;
import org.fogbowcloud.manager.core.exceptions.UnauthenticatedUserException;
import org.fogbowcloud.manager.core.plugins.behavior.federationidentity.FederationIdentityPlugin;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=", commandDescription = "Token manipulation")
public class CommandToken extends CommandAuthentication {

	public static final String NAME = "token";

	@Parameter(names = { "--create", "-c" }, description = "Create a new token", required = true)
	private Boolean isCreate = false;

	@DynamicParameter(names = "-D", description = "Dynamic parameters")
	private Map<String, String> credentials = new HashMap<>();

	public String run() throws ReflectiveOperationException, TokenValueCreationException, UnauthenticatedUserException {
		if (this.isCreate) {
			HomeDir.getInstance().setPath(getConfPath());
			return createToken();
		}

		return null;
	}

	private String createToken()
			throws ReflectiveOperationException, TokenValueCreationException, UnauthenticatedUserException {

		FederationIdentityPlugin identityPlugin = getFederationIdentityPlugin();
		Map<String, String> userCredentials = new HashMap<>();

		for (String key : this.credentials.keySet()) {
			userCredentials.put(key, this.credentials.get(key));
		}

		String accessId = identityPlugin.createFederationTokenValue(userCredentials);
		return accessId;
	}
}
