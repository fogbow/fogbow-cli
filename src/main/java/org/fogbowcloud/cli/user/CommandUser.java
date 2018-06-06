package org.fogbowcloud.cli.user;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.gson.Gson;
import org.fogbowcloud.manager.core.exceptions.UnauthenticatedException;
import org.fogbowcloud.manager.core.models.token.FederationUser;
import org.fogbowcloud.manager.core.plugins.behavior.federationidentity.FederationIdentityPlugin;
import org.fogbowcloud.manager.core.plugins.exceptions.TokenValueCreationException;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.Constructor;
import java.util.Properties;
import java.util.Set;

@Parameters(separators = "=", commandDescription = "Token manipulation")
public class CommandUser {

    public static final String NAME = "user";

    private static final String PLUGIN_PACKAGE = "org.fogbowcloud.manager.core.manager.plugins.identity";

    @Parameter(names = { "--get-user", "-g" }, description = "Get user information", required = true)
    private Boolean isGetUser = false;

    @Parameter(names = { "--type", "-t" }, description = "Identity plugin type", required = true)
    private String identityPluginName = null;

    @Parameter(names = { "--federation-token-value", "-f" }, description = "Federation token value", required = true)
    private String federationTokenValue = null;

    public String run() throws ReflectiveOperationException, UnauthenticatedException, TokenValueCreationException {
        if (this.isGetUser) {
            FederationUser federationUser = getFederationUser();
            return userToJson(federationUser);
        }

        return null;
    }

    private FederationUser getFederationUser()
            throws TokenValueCreationException, ReflectiveOperationException, UnauthenticatedException {

        FederationIdentityPlugin identityPlugin = getFederationIdentityPlugin();
        FederationUser federationUser = identityPlugin.getFederationUser(this.federationTokenValue);
        return federationUser;
    }

    protected FederationIdentityPlugin getFederationIdentityPlugin()
            throws ReflectiveOperationException, TokenValueCreationException {
        Class<? extends FederationIdentityPlugin> federationIdentityPluginClass = getFederationIdentityPluginClass(
                this.identityPluginName);
        Constructor<?> constructor = federationIdentityPluginClass.getConstructor(Properties.class);
        FederationIdentityPlugin identityPlugin = (FederationIdentityPlugin) constructor.newInstance(new Properties());
        return identityPlugin;
    }

    private Class<? extends FederationIdentityPlugin> getFederationIdentityPluginClass(String typeName)
            throws TokenValueCreationException {
        Reflections reflections = new Reflections(ClasspathHelper.forPackage(PLUGIN_PACKAGE), new SubTypesScanner());
        Set<Class<? extends FederationIdentityPlugin>> allFederationIdentityPluginClasses = reflections
                .getSubTypesOf(FederationIdentityPlugin.class);

        for (Class<? extends FederationIdentityPlugin> eachClass : allFederationIdentityPluginClasses) {
            String federationIdentityPluginName = getFederationIdentityPluginName(eachClass);
            if (federationIdentityPluginName.equals(typeName)) {
                return eachClass;
            }
        }

        throw new TokenValueCreationException("Token type [" + typeName + "] is not valid.");
    }

    private String getFederationIdentityPluginName(Class<? extends FederationIdentityPlugin> classObject) {
        String packageName = classObject.getName();
        String className = packageName.substring(packageName.lastIndexOf(".") + 1);
        String identityPluginSuffix = "IdentityPlugin";
        String identityPluginName = className.replace(identityPluginSuffix, "");
        identityPluginName = identityPluginName.toLowerCase();
        return identityPluginName;
    }

    protected String userToJson(FederationUser federationUser) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(federationUser);

        return jsonString;
    }
}
