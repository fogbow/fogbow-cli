package org.fogbowcloud.cli.authentication.user;

import org.fogbowcloud.cli.authentication.CommandAuthentication;
import org.fogbowcloud.manager.core.HomeDir;
import org.fogbowcloud.manager.core.exceptions.TokenValueCreationException;
import org.fogbowcloud.manager.core.exceptions.UnauthenticatedUserException;
import org.fogbowcloud.manager.core.exceptions.UnexpectedException;
import org.fogbowcloud.manager.core.models.tokens.FederationUser;
import org.fogbowcloud.manager.core.plugins.behavior.federationidentity.FederationIdentityPlugin;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.gson.Gson;

@Parameters(separators = "=", commandDescription = "Token manipulation")
public class CommandUser extends CommandAuthentication {

    public static final String NAME = "user";

    @Parameter(names = { "--get-user", "-g" }, description = "Get user information", required = true)
    private Boolean isGetUser = false;

    @Parameter(names = { "--federation-token-value", "-f" }, description = "Federation token value", required = true)
    private String federationTokenValue = null;

    public String run() throws ReflectiveOperationException, TokenValueCreationException, UnauthenticatedUserException, UnexpectedException {
        if (this.isGetUser) {
            HomeDir.getInstance().setPath(getConfPath());
            FederationUser federationUser = getFederationUser();
            return userToJson(federationUser);
        }

        return null;
    }

    private FederationUser getFederationUser()
            throws TokenValueCreationException, ReflectiveOperationException, UnauthenticatedUserException, UnexpectedException {

        FederationIdentityPlugin identityPlugin = getFederationIdentityPlugin();
        FederationUser federationUser = identityPlugin.getFederationUser(this.federationTokenValue);
        return federationUser;
    }

    protected String userToJson(FederationUser federationUser) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(federationUser);

        return jsonString;
    }
}
