package org.fogbowcloud.cli.token;

import java.lang.reflect.Constructor;
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
	
	public static final String NAME = "token";
	
	private static final String PLUGIN_PACKAGE = "org.fogbowcloud.manager.core.plugins.identity";

	@Parameter(names = { "--create", "-c" }, description = "Create a new token", required = true)
	private Boolean isCreation = false;

	@Parameter(names = { "--type", "-t" }, description = "Identity plugin type", required = true)
	private String identityPluginType = null;

	@DynamicParameter(names = "-D", description = "Dynamic parameters")
	private Map<String, String> credentials = new HashMap<String, String>();

	public String run() throws ReflectiveOperationException, UnauthorizedException, TokenCreationException {
		if (isCreation) {
			return createToken();
		}
		return null;
	}

	private Class<? extends IdentityPlugin> getIdentityPluginClassByTypeName(String typeName)
			throws TokenCreationException {
		Reflections reflections = new Reflections(ClasspathHelper.forPackage(PLUGIN_PACKAGE), new SubTypesScanner());
		Set<Class<? extends IdentityPlugin>> allIdentityPluginClasses = reflections.getSubTypesOf(IdentityPlugin.class);

		for (Class<? extends IdentityPlugin> eachClass : allIdentityPluginClasses) {
			String identityPluginName = getIdentityPluginName(eachClass);
			if (identityPluginName.equals(typeName)) {
				return eachClass;
			}
		}

		throw new TokenCreationException("Token type [" + typeName + "] is not valid.");
	}

	protected IdentityPlugin getIdentityPlugin() throws ReflectiveOperationException, TokenCreationException {
		Class<? extends IdentityPlugin> identityPluginClass = getIdentityPluginClassByTypeName(this.identityPluginType);
		Constructor<?> constructor = identityPluginClass.getConstructor(Properties.class);
		IdentityPlugin identityPlugin = (IdentityPlugin) constructor.newInstance(new Properties());
		return identityPlugin;
	}

	private String createToken() throws ReflectiveOperationException, UnauthorizedException, TokenCreationException {

		IdentityPlugin identityPlugin = getIdentityPlugin();
		Map<String, String> userCredentials = new HashMap<String, String>();

		for (String key : this.credentials.keySet()) {
			userCredentials.put(key, this.credentials.get(key));
		}

		String accessId = identityPlugin.createToken(userCredentials).getAccessId();
		return accessId;
	}

	private String getIdentityPluginName(Class<? extends IdentityPlugin> classObject) {
		String packageName = classObject.getName();
		String className = packageName.substring(packageName.lastIndexOf(".") + 1);
		String identityPluginSuffix = "IdentityPlugin";
		String identityPluginName = className.replace(identityPluginSuffix, "");
		identityPluginName = identityPluginName.toLowerCase();
		return identityPluginName;
	}

	protected void setIdentityPluginType(String identityPluginType) {
		this.identityPluginType = identityPluginType;
	}

	protected void setIsCreation(Boolean isCreation) {
		this.isCreation = isCreation;
	}
	
	protected void addCredential(String value, String key) {
		credentials.put(value,  key);
	}
	
}
