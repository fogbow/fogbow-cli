package org.fogbowcloud.cli;

import java.lang.reflect.Constructor;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.fogbowcloud.manager.core.plugins.IdentityPlugin;
import org.fogbowcloud.manager.core.plugins.identity.exceptions.TokenCreationException;
import org.fogbowcloud.manager.core.plugins.identity.exceptions.UnauthorizedException;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=", commandDescription = "Token manipulation")
public class CommandToken {

	private static final String PLUGIN_PACKAGE = "org.fogbowcloud.manager.core.plugins.identity";

	@Parameter(names = { "--create", "-c" }, description = "Create a new token", required = true)
	private Boolean isCreation = false;

	@Parameter(names = { "--type", "-t" }, description = "Identity plugin type", required = true)
	private String identityPluginType = null;
	
	@DynamicParameter(names = "-D", description = "Dynamic parameters")
	Map<String, String> credentials = new HashMap<String, String>();

	public String run() throws ReflectiveOperationException, UnauthorizedException, TokenCreationException {
		if (isCreation) {
			return createToken();
		}
		return null;
	}

	private Class<? extends IdentityPlugin> getIdentityPluginClassByTypeName(String typeName) {
		Reflections reflections = new Reflections(ClasspathHelper.forPackage(PLUGIN_PACKAGE), new SubTypesScanner());
		Set<Class<? extends IdentityPlugin>> allClasses = reflections.getSubTypesOf(IdentityPlugin.class);

		for (Class<? extends IdentityPlugin> currentClass : allClasses) {
			if (currentClass.getName().contains(typeName)) {
				return currentClass;
			}
		}

		throw new InvalidParameterException("Token type [" + typeName + "] is not valid.");
	}
	
	private IdentityPlugin getIdentityPlugin() throws ReflectiveOperationException {
		Class<? extends IdentityPlugin> identityPluginClass = getIdentityPluginClassByTypeName(this.identityPluginType);
		Constructor<?> constructor = identityPluginClass.getConstructor(Properties.class);
		IdentityPlugin identityPlugin = (IdentityPlugin) constructor.newInstance(new Properties());
		return identityPlugin;
	}
	
	private String createToken() throws ReflectiveOperationException, UnauthorizedException, TokenCreationException {
		
		IdentityPlugin identityPlugin = getIdentityPlugin();
		Map<String, String> userCredentials = new HashMap<String, String>();
		
		for (String key: this.credentials.keySet()) {
			userCredentials.put(key, this.credentials.get(key));
		}
		
		String accessId = identityPlugin.createToken(userCredentials).getAccessId();
		return accessId;
	}
}
