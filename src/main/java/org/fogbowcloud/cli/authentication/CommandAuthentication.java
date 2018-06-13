package org.fogbowcloud.cli.authentication;

import com.beust.jcommander.Parameter;
import org.fogbowcloud.manager.core.plugins.behavior.federationidentity.FederationIdentityPlugin;
import org.fogbowcloud.manager.core.plugins.exceptions.TokenValueCreationException;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.Set;

public class CommandAuthentication {

    private static final String ENV_VAR_NAME = "FOGBOW_CONF_PATH";

    @Parameter(names = { "--conf-path", "-p" }, description = "Configuration file path")
    private String confPath = null;

    @Parameter(names = { "--type", "-t" }, description = "Identity plugin type", required = true)
    private String identityPluginName = null;

    public FederationIdentityPlugin getFederationIdentityPlugin()
            throws ReflectiveOperationException, TokenValueCreationException {
        Class<? extends FederationIdentityPlugin> federationIdentityPluginClass = getFederationIdentityPluginClass();
        Constructor<?> constructor = federationIdentityPluginClass.getConstructor();

        FederationIdentityPlugin identityPlugin = (FederationIdentityPlugin) constructor.newInstance();
        return identityPlugin;
    }

    private Class<? extends FederationIdentityPlugin> getFederationIdentityPluginClass()
            throws TokenValueCreationException {
        Reflections reflections = new Reflections();
        Set<Class<? extends FederationIdentityPlugin>> allFederationIdentityPluginClasses = reflections
                .getSubTypesOf(FederationIdentityPlugin.class);

        for (Class<? extends FederationIdentityPlugin> eachClass : allFederationIdentityPluginClasses) {
            String federationIdentityPluginName = getFederationIdentityPluginName(eachClass);
            if (federationIdentityPluginName.equals(this.identityPluginName)) {
                return eachClass;
            }
        }

        throw new TokenValueCreationException("Token type [" + this.identityPluginName + "] is not valid.");
    }

    private String getFederationIdentityPluginName(Class<? extends FederationIdentityPlugin> classObject) {
        String packageName = classObject.getName();
        String className = packageName.substring(packageName.lastIndexOf(".") + 1);
        String identityPluginSuffix = "IdentityPlugin";
        String identityPluginName = className.replace(identityPluginSuffix, "");
        identityPluginName = identityPluginName.toLowerCase();
        return identityPluginName;
    }

    protected String getConfPath() {
        return (this.confPath == null) ? System.getenv(ENV_VAR_NAME) : this.confPath;
    }
}
