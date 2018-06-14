package org.fogbowcloud.cli.authentication.token;

import com.beust.jcommander.Parameter;
import org.fogbowcloud.cli.authentication.CommandAuthentication;
import org.fogbowcloud.manager.core.HomeDir;
import org.fogbowcloud.manager.core.plugins.behavior.federationidentity.FederationIdentityPlugin;
import org.fogbowcloud.manager.core.plugins.exceptions.TokenValueCreationException;

public class CommandCheckToken extends CommandAuthentication {

    public static final String NAME = "check-token";

    private static final String TOKEN_VALID = "Token Valid";
    private static final String TOKEN_INVALID = "Token Unauthorized";

    @Parameter(names = { "--federation-token-value", "-f" }, description = "Federation token value", required = true)
    private String federationTokenValue = null;

    public String run() throws TokenValueCreationException, ReflectiveOperationException {
        HomeDir.getInstance().setPath(getConfPath());
        return checkToken();
    }

    private String checkToken() throws TokenValueCreationException, ReflectiveOperationException {
        FederationIdentityPlugin identityPlugin = getFederationIdentityPlugin();

        boolean isValid = identityPlugin.isValid(this.federationTokenValue);

        if (isValid) {
            return TOKEN_VALID;
        }

        return TOKEN_INVALID;
    }
}
