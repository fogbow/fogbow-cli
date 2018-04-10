package org.fogbowcloud.cli;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.fogbowcloud.manager.core.plugins.IdentityPlugin;
import org.fogbowcloud.manager.core.plugins.util.Credential;
import org.fogbowcloud.manager.core.util.UserdataUtils;
import org.fogbowcloud.manager.occi.OCCIConstants;
import org.fogbowcloud.manager.occi.OCCIConstants.NetworkAllocation;
import org.fogbowcloud.manager.occi.model.HeaderUtils;
import org.fogbowcloud.manager.occi.model.OCCIException;
import org.fogbowcloud.manager.occi.model.OCCIHeaders;
import org.fogbowcloud.manager.occi.model.Token;
import org.fogbowcloud.manager.occi.order.OrderAttribute;
import org.fogbowcloud.manager.occi.order.OrderConstants;
import org.fogbowcloud.manager.occi.storage.StorageAttribute;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.base.Joiner;

@SuppressWarnings("deprecation")
public class Main {

	protected static final String LOCAL_TOKEN_HEADER = "local_token";
	protected static final String PLUGIN_PACKAGE = "org.fogbowcloud.manager.core.plugins.identity";
	protected static final String DEFAULT_URL = "http://localhost:8182";
	protected static final int DEFAULT_INTANCE_COUNT = 1;
	protected static final String DEFAULT_TYPE = OrderConstants.DEFAULT_TYPE;
	protected static final String DEFAULT_IMAGE = "fogbow-linux-x86";

	private static HttpClient client;
	private static IdentityPlugin identityPlugin;

	public static void main(String[] args) throws Exception {
		configureLog4j();

		JCommander jc = new JCommander();

		// Normalize args
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("\"") && args[i].endsWith("\"")) {
				args[i] = args[i].replace("\"", "\"\"");
			}
		}

		MemberCommand member = new MemberCommand();
		jc.addCommand("member", member);
		OrderCommand order = new OrderCommand();
		jc.addCommand("order", order);
		InstanceCommand instance = new InstanceCommand();
		jc.addCommand("instance", instance);
		TokenCommand token = new TokenCommand();
		jc.addCommand("token", token);
		ResourceCommand resource = new ResourceCommand();
		jc.addCommand("resource", resource);
		StorageCommand storage = new StorageCommand();
		jc.addCommand("storage", storage);
		NetworkCommand network = new NetworkCommand();
		jc.addCommand("network", network);
		AttachmentCommand attachment = new AttachmentCommand();
		jc.addCommand("attachment", attachment);
		AccountingCommand accounting = new AccountingCommand();
		jc.addCommand("accounting", accounting);

		jc.setProgramName("fogbow-cli");
		try {
			jc.parse(args);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			jc.usage();
			return;
		}

		String parsedCommand = jc.getParsedCommand();

		if (parsedCommand == null) {
			jc.usage();
			return;
		}

		if (parsedCommand.equals("order")) {
			String url = order.url;

			String authToken = normalizeTokenFile(order.authFile);
			if (authToken == null) {
				authToken = normalizeToken(order.authToken);
			}

			if (order.create) {
				if (order.delete || order.get || order.orderId != null) {
					jc.usage();
					return;
				}

				if (!order.type.equals("one-time") && !order.type.equals("persistent")) {
					jc.usage();
					return;
				}

				List<Header> headers = new LinkedList<Header>();
				headers.add(new BasicHeader("Category", OrderConstants.TERM + "; scheme=\"" + OrderConstants.SCHEME
						+ "\"; class=\"" + OrderConstants.KIND_CLASS + "\""));
				headers.add(new BasicHeader("X-OCCI-Attribute",
						OrderAttribute.INSTANCE_COUNT.getValue() + "=" + order.instanceCount));
				headers.add(new BasicHeader("X-OCCI-Attribute", OrderAttribute.TYPE.getValue() + "=" + order.type));

				if (order.resourceKind != null && order.resourceKind.equals(OrderConstants.COMPUTE_TERM)) {
					if (order.flavor != null && !order.flavor.isEmpty()) {
						headers.add(new BasicHeader("Category",
								order.flavor + "; scheme=\"" + OrderConstants.TEMPLATE_RESOURCE_SCHEME + "\"; class=\""
										+ OrderConstants.MIXIN_CLASS + "\""));
					}
					headers.add(new BasicHeader("Category", order.image + "; scheme=\""
							+ OrderConstants.TEMPLATE_OS_SCHEME + "\"; class=\"" + OrderConstants.MIXIN_CLASS + "\""));

					if (order.userDataFile != null && !order.userDataFile.isEmpty()) {
						if (order.userDataFileContentType == null || order.userDataFileContentType.isEmpty()) {
							System.out.println("Content type of user data file cannot be empty.");
							return;
						}
						try {
							String userDataContent = getFileContent(order.userDataFile);
							String userData = userDataContent.replace("\n", UserdataUtils.USER_DATA_LINE_BREAKER);
							userData = new String(Base64.encodeBase64(userData.getBytes()));
							headers.add(new BasicHeader("X-OCCI-Attribute",
									OrderAttribute.EXTRA_USER_DATA_ATT.getValue() + "=" + userData));
							headers.add(new BasicHeader("X-OCCI-Attribute",
									OrderAttribute.EXTRA_USER_DATA_CONTENT_TYPE_ATT.getValue() + "="
											+ order.userDataFileContentType));
						} catch (IOException e) {
							System.out.println("User data file not found.");
							return;
						}
					}

					if (order.publicKey != null && !order.publicKey.isEmpty()) {

						try {
							order.publicKey = getFileContent(order.publicKey);
						} catch (IOException e) {
							System.out.println("Public key file not found.");
							return;
						}

						headers.add(new BasicHeader("Category",
								OrderConstants.PUBLIC_KEY_TERM + "; scheme=\""
										+ OrderConstants.CREDENTIALS_RESOURCE_SCHEME + "\"; class=\""
										+ OrderConstants.MIXIN_CLASS + "\""));
						headers.add(new BasicHeader("X-OCCI-Attribute",
								OrderAttribute.DATA_PUBLIC_KEY.getValue() + "=" + order.publicKey));
					}

					if (order.network != null && !order.network.isEmpty()) {
						headers.add(new BasicHeader("Link",
								"</" + OrderConstants.NETWORK_TERM + "/" + order.network + ">; rel=\""
										+ OrderConstants.INFRASTRUCTURE_OCCI_SCHEME + OrderConstants.NETWORK_TERM
										+ "\"; category=\"" + OrderConstants.INFRASTRUCTURE_OCCI_SCHEME
										+ OrderConstants.NETWORK_INTERFACE_TERM + "\";"));
					}

				}

				headers.add(new BasicHeader("X-OCCI-Attribute",
						OrderAttribute.RESOURCE_KIND.getValue() + "=" + order.resourceKind));

				if (order.requirements != null) {
					String requirements = Joiner.on(" ").join(order.requirements);
					if (requirements.isEmpty()) {
						System.out.println("Requirements empty.");
						jc.usage();
						return;
					}
					headers.add(new BasicHeader("X-OCCI-Attribute",
							OrderAttribute.REQUIREMENTS.getValue() + "=" + requirements));
				}

				doRequest("post", url + "/" + OrderConstants.TERM, authToken, headers);
			}
		} 
	}

	private static boolean isValidAllocation(String allocation) {
		for (NetworkAllocation networkAllocation : OCCIConstants.NetworkAllocation.values()) {
			if (allocation.equals(networkAllocation.getValue())) {
				return true;
			}
		}
		return false;
	}

	private static void configureLog4j() {
		ConsoleAppender console = new ConsoleAppender();
		console.setThreshold(Level.OFF);
		console.activateOptions();
		Logger.getRootLogger().addAppender(console);
	}

	public static void setIdentityPlugin(IdentityPlugin identityPlugin) {
		Main.identityPlugin = identityPlugin;
	}

	public static IdentityPlugin getIdentityPlugin() {
		return identityPlugin;
	}

	@SuppressWarnings("resource")
	private static String getFileContent(String path) throws IOException {
		FileReader reader = new FileReader(path);
		BufferedReader leitor = new BufferedReader(reader);
		String fileContent = "";
		String linha = "";
		while (true) {
			linha = leitor.readLine();
			if (linha == null)
				break;
			fileContent += linha + "\n";
		}
		return fileContent.trim();
	}

	protected static Class<?> getClassOfTokenType(String tokenType) {
		Reflections reflections = new Reflections(ClasspathHelper.forPackage(PLUGIN_PACKAGE), new SubTypesScanner());

		Set<Class<? extends IdentityPlugin>> allClasses = reflections.getSubTypesOf(IdentityPlugin.class);
		for (Class<? extends IdentityPlugin> eachClass : allClasses) {
			String[] packageName = eachClass.getName().split("\\.");
			String type = packageName[packageName.length - 2];
			if (type.equals(tokenType)) {
				return eachClass;
			}
		}
		return null;
	}

	protected static List<String> getNamePossiblePluginTypes() {
		Reflections reflections = new Reflections(ClasspathHelper.forPackage(PLUGIN_PACKAGE), new SubTypesScanner());

		Set<Class<? extends IdentityPlugin>> allClasses = reflections.getSubTypesOf(IdentityPlugin.class);
		List<String> possibleTypes = new LinkedList<String>();
		for (Class<? extends IdentityPlugin> eachClass : allClasses) {
			String[] packageName = eachClass.getName().split("\\.");
			String type = packageName[packageName.length - 2];
			possibleTypes.add(type);
		}
		return possibleTypes;
	}

	protected static String getTokenInfo(TokenCommand token) {

		Class<?> pluginClass = getClassOfTokenType(token.type);

		try {
			if (identityPlugin == null) {
				Map<String, String> credentials = token.credentials;
				Properties properties = new Properties();
				for (Entry<String, String> credEntry : credentials.entrySet()) {
					properties.put(credEntry.getKey(), credEntry.getValue());
				}
				identityPlugin = (IdentityPlugin) createInstance(pluginClass, properties);
			}

			try {
				Token tokenInfo = identityPlugin.getToken(token.token);
				if (token.accessId == false && token.userName == false && token.attributes == false
						&& token.userId == false) {
					return tokenInfo.toString();
				}

				String responseStr = "";
				if (token.accessId) {
					responseStr = tokenInfo.getAccessId();
				}
				if (token.userName) {
					if (!responseStr.isEmpty()) {
						responseStr += ",";
					}
					responseStr += tokenInfo.getUser().getName();
				}
				if (token.userId) {
					if (!responseStr.isEmpty()) {
						responseStr += ",";
					}
					responseStr += tokenInfo.getUser().getId();
				}
				if (token.attributes) {
					if (!responseStr.isEmpty()) {
						responseStr += ",";
					}
					responseStr += tokenInfo.getAttributes();
				}

				return responseStr;
			} catch (Exception e) {
				// Do Nothing
			}

		} catch (Exception e) {
			return "Token type [" + token.type + "] is not valid. " + "Possible types: " + getNamePossiblePluginTypes()
					+ ".";
		}
		return "No Result.";
	}

	protected static String checkToken(TokenCommand token) {

		Class<?> pluginClass = getClassOfTokenType(token.type);

		try {
			if (identityPlugin == null) {
				Map<String, String> credentials = token.credentials;
				Properties properties = new Properties();
				for (Entry<String, String> credEntry : credentials.entrySet()) {
					properties.put(credEntry.getKey(), credEntry.getValue());
				}
				identityPlugin = (IdentityPlugin) createInstance(pluginClass, properties);
			}
		} catch (Exception e) {
			return "Token type [" + token.type + "] is not valid. " + "Possible types: " + getNamePossiblePluginTypes()
					+ ".";
		}

		try {
			boolean isValid = identityPlugin.isValid(token.token);
			if (isValid) {
				return "Token Valid";
			} else {
				return "Token Unauthorized";
			}
		} catch (Exception e) {
			return "Token Unauthorized";
		}
	}

	protected static String createToken(TokenCommand token) {
		Reflections reflections = new Reflections(ClasspathHelper.forPackage(PLUGIN_PACKAGE), new SubTypesScanner());

		Set<Class<? extends IdentityPlugin>> allClasses = reflections.getSubTypesOf(IdentityPlugin.class);

		Class<?> pluginClass = getClassOfTokenType(token.type);

		if (pluginClass == null) {
			return "Token type [" + token.type + "] is not valid. " + "Possible types: " + getNamePossiblePluginTypes()
					+ ".";
		}

		try {
			if (identityPlugin == null) {
				identityPlugin = (IdentityPlugin) createInstance(pluginClass, new Properties());
			}
		} catch (Exception e) {
			return e.getMessage() + "\n" + getPluginCredentialsInformation(allClasses);
		}

		try {
			Token createToken = identityPlugin.createToken(token.credentials);
			return generateResponse(createToken);
		} catch (OCCIException e) {
			if (e.getStatus() == null) {
				return "OCCI error: while trying to sign the token";
			}
			return e.getStatus().getDescription() + "\n" + getPluginCredentialsInformation(allClasses);
		} catch (Exception e) {
			return e.getMessage() + "\n" + getPluginCredentialsInformation(allClasses);
		} 
	}

	protected static String getPluginCredentialsInformation(Set<Class<? extends IdentityPlugin>> allClasses) {
		StringBuilder response = new StringBuilder();
		response.append("Credentials :\n");

		for (Class<? extends IdentityPlugin> eachClass : allClasses) {
			String[] identityPluginFullName = eachClass.getName().split("\\.");
			System.out.println(eachClass.getName());
			IdentityPlugin identityPlugin = null;

			try {
				identityPlugin = (IdentityPlugin) createInstance(eachClass, new Properties());
			} catch (Exception e) {
			}

			if (identityPlugin.getCredentials() == null) {
				continue;
			}

			response.append("* " + identityPluginFullName[identityPluginFullName.length - 1] + "\n");

			for (Credential credential : identityPlugin.getCredentials()) {
				String valueDefault = "";
				if (credential.getValueDefault() != null) {
					valueDefault = " - default :" + credential.getValueDefault();
				}
				String feature = "Optional";
				if (credential.isRequired()) {
					feature = "Required";
				}
				response.append("   -D" + credential.getName() + " (" + feature + ")" + valueDefault + "\n");
			}

		}
		return response.toString().trim();
	}

	protected static Credential[] getPluginCredentialsByTokenType(TokenCommand token) {

		Class<?> pluginClass = getClassOfTokenType(token.type);

		if (pluginClass == null) {
			return null;
		}

		try {
			return ((IdentityPlugin) createInstance(pluginClass, new Properties())).getCredentials();
		} catch (Exception e) {
			return null;
		}

	}

	private static String generateResponse(Token token) {
		if (token == null) {
			return new String();
		}
		return token.getAccessId();
	}

	protected static Object createInstance(Class<?> pluginClass, Properties properties) throws Exception {
		return pluginClass.getConstructor(Properties.class).newInstance(properties);
	}

	protected static String normalizeToken(String token) {
		if (token == null) {
			return null;
		}
		return token.replace("\n", "");
	}

	protected static String normalizeTokenFile(String token) {
		if (token == null) {
			return null;
		}
		File tokenFile = new File(token);
		if (tokenFile.exists()) {
			try {
				token = IOUtils.toString(new FileInputStream(tokenFile));
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
		return token.replace("\n", "");
	}

	private static void doRequest(String method, String endpoint, String authToken)
			throws URISyntaxException, HttpException, IOException {
		doRequest(method, endpoint, authToken, new LinkedList<Header>());
	}

	private static void doRequest(String method, String endpoint, String authToken, List<Header> additionalHeaders)
			throws URISyntaxException, HttpException, IOException {
		HttpUriRequest request = null;
		if (method.equals("get")) {
			request = new HttpGet(endpoint);
		} else if (method.equals("delete")) {
			request = new HttpDelete(endpoint);
		} else if (method.equals("post")) {
			request = new HttpPost(endpoint);
		}
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		if (authToken != null) {
			request.addHeader(OCCIHeaders.X_AUTH_TOKEN, authToken);
		}
		for (Header header : additionalHeaders) {
			request.addHeader(header);
		}

		if (client == null) {
			client = new DefaultHttpClient();
			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			client = new DefaultHttpClient(
					new ThreadSafeClientConnManager(params, client.getConnectionManager().getSchemeRegistry()), params);
		}

		HttpResponse response = client.execute(request);

		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK
				|| response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
			Header locationHeader = getLocationHeader(response.getAllHeaders());
			if (locationHeader != null && locationHeader.getValue().contains(OrderConstants.TERM)) {
				System.out.println(generateLocationHeaderResponse(locationHeader));
			} else {
				if (method.equals("post")) {
					System.out.println(generateLocationHeaderResponse(locationHeader));
				} else {
					System.out.println(EntityUtils.toString(response.getEntity()));
				}
			}
		} else {
			System.out.println(response.getStatusLine().toString());
		}
	}

	protected static Header getLocationHeader(Header[] headers) {
		Header locationHeader = null;
		for (Header header : headers) {
			if (header.getName().equals("Location")) {
				locationHeader = header;
			}
		}
		return locationHeader;
	}

	protected static String generateLocationHeaderResponse(Header header) {
		String[] locations = header.getValue().split(",");
		String response = "";
		for (String location : locations) {
			response += HeaderUtils.X_OCCI_LOCATION_PREFIX + location + "\n";
		}
		return response.trim();
	}

	public static void setClient(HttpClient client) {
		Main.client = client;
	}

	private static class Command {
		@Parameter(names = "--url", description = "fogbow manager url")
		String url = System.getenv("FOGBOW_URL") == null ? Main.DEFAULT_URL : System.getenv("FOGBOW_URL");
	}

	private static class AuthedCommand extends Command {
		@Parameter(names = "--auth-token", description = "auth token")
		String authToken = null;

		@Parameter(names = "--auth-file", description = "auth file")
		String authFile = null;
	}

	@Parameters(separators = "=", commandDescription = "Members operations")
	private static class MemberCommand extends AuthedCommand {
		@Parameter(names = "--quota", description = "Quota")
		Boolean quota = false;

		@Parameter(names = "--id", description = "Member Id")
		String memberId = null;

		@Parameter(names = "--usage", description = "Usage")
		Boolean usage = false;
	}

	@Parameters(separators = "=", commandDescription = "Usage consults")
	private static class UsageCommand extends AuthedCommand {
		@Parameter(names = "--members", description = "List members' usage")
		Boolean members = false;

		@Parameter(names = "--users", description = "List users' usage")
		Boolean users = false;
	}

	@Parameters(separators = "=", commandDescription = "Accounting consult")
	private static class AccountingCommand extends AuthedCommand {
		// There aren't specific commands to this one
	}

	@Parameters(separators = "=", commandDescription = "Order operations")
	private static class OrderCommand extends AuthedCommand {
		@Parameter(names = "--get", description = "Get order")
		Boolean get = false;

		@Parameter(names = "--create", description = "Create order")
		Boolean create = false;

		@Parameter(names = "--delete", description = "Delete order")
		Boolean delete = false;

		@Parameter(names = "--id", description = "Order id")
		String orderId = null;

		@Parameter(names = "--n", description = "Instance count")
		int instanceCount = Main.DEFAULT_INTANCE_COUNT;

		@Parameter(names = "--image", description = "Instance image")
		String image = Main.DEFAULT_IMAGE;

		@Parameter(names = "--flavor", description = "Instance flavor")
		String flavor = null;

		@Parameter(names = "--type", description = "Order type (one-time|persistent)")
		String type = Main.DEFAULT_TYPE;

		@Parameter(names = "--public-key", description = "Public key")
		String publicKey = null;

		@Parameter(names = "--requirements", description = "Requirements", variableArity = true)
		List<String> requirements = null;

		@Parameter(names = "--user-data-file", description = "User data file for cloud init")
		String userDataFile = null;

		@Parameter(names = "--user-data-file-content-type", description = "Content type of user data file for cloud init")
		String userDataFileContentType = null;

		@Parameter(names = "--size", description = "Size instance storage")
		String size = null;

		@Parameter(names = "--resource-kind", description = "Resource kind")
		String resourceKind = null;

		@Parameter(names = "--network", description = "Network id")
		String network = null;

		@Parameter(names = "--cidr", description = "CIDR")
		String cidr = null;

		@Parameter(names = "--gateway", description = "Gateway")
		String gateway = null;

		@Parameter(names = "--allocation", description = "Allocation (dynamicy or static)")
		String allocation = null;
	}

	@Parameters(separators = "=", commandDescription = "Instance operations")
	private static class InstanceCommand extends AuthedCommand {
		@Parameter(names = "--get", description = "Get instance data")
		Boolean get = false;

		@Parameter(names = "--delete", description = "Delete instance")
		Boolean delete = false;

		@Parameter(names = "--create", description = "Create instance directly")
		Boolean create = false;

		@Parameter(names = "--id", description = "Instance id")
		String instanceId = null;

		@Parameter(names = "--flavor", description = "Instance flavor")
		String flavor = null;

		@Parameter(names = "--image", description = "Instance image")
		String image = null;

		@Parameter(names = "--user-data-file", description = "User data file for cloud init")
		String userDataFile = null;

		@Parameter(names = "--public-key", description = "Public key")
		String publicKey = null;
	}

	@Parameters(separators = "=", commandDescription = "Instance storage operations")
	private static class StorageCommand extends AuthedCommand {

		@Parameter(names = "--create", description = "Create instance storage")
		Boolean create = false;

		@Parameter(names = "--get", description = "Get instance storage")
		Boolean get = false;

		@Parameter(names = "--delete", description = "Delete instance storage")
		Boolean delete = false;

		@Parameter(names = "--id", description = "Instance storage id")
		String storageId = null;

		@Parameter(names = "--size", description = "Size instance storage")
		String size = null;
	}

	@Parameters(separators = "=", commandDescription = "Instance network operations")
	private static class NetworkCommand extends AuthedCommand {
		@Parameter(names = "--get", description = "Get instance network")
		Boolean get = false;

		@Parameter(names = "--create", description = "Post new network instance")
		Boolean create = false;

		@Parameter(names = "--delete", description = "Delete instance network")
		Boolean delete = false;

		@Parameter(names = "--id", description = "Instance network id")
		String networkId = null;

		@Parameter(names = "--cidr", description = "CIDR")
		String cidr = null;

		@Parameter(names = "--gateway", description = "Gateway")
		String gateway = null;

		@Parameter(names = "--allocation", description = "Allocation (dynamicy or static)")
		String allocation = null;
	}

	@Parameters(separators = "=", commandDescription = "Attachment operations")
	private static class AttachmentCommand extends AuthedCommand {
		@Parameter(names = "--create", description = "Attachment create")
		Boolean create = false;

		@Parameter(names = "--delete", description = "Attachment delete")
		Boolean delete = false;

		@Parameter(names = "--get", description = "Get attachment")
		Boolean get = false;

		@Parameter(names = "--id", description = "Attachment id")
		String id = null;

		@Parameter(names = "--storageId", description = "Storage id attribute")
		String storageId = null;

		@Parameter(names = "--computeId", description = "Compute id attribute")
		String computeId = null;

		@Parameter(names = "--mountPoint", description = "Mount point attribute")
		String mountPoint = null;
	}

	@Parameters(separators = "=", commandDescription = "Token operations")
	protected static class TokenCommand {
		@Parameter(names = "--create", description = "Create token")
		Boolean create = false;

		@Parameter(names = "--type", description = "Token type")
		String type = null;

		@DynamicParameter(names = "-D", description = "Dynamic parameters")
		Map<String, String> credentials = new HashMap<String, String>();

		@Parameter(names = "--check", description = "Check token")
		Boolean check = false;

		@Parameter(names = "--info", description = "Get Info")
		Boolean info = false;

		@Parameter(names = "--token", description = "Token Pure")
		String token = null;

		@Parameter(names = "--user", description = "User name")
		boolean userName = false;

		@Parameter(names = "--user-id", description = "User id")
		boolean userId = false;

		@Parameter(names = "--access-id", description = "Access Id information")
		boolean accessId = false;

		@Parameter(names = "--attributes", description = "Attributes information")
		boolean attributes = false;

		protected boolean hasPassword() {
			return credentials.containsKey("password");
		}

		protected void putPasswordAtCredentials(String password) {
			credentials.put("password", password);
		}

		protected void addPossibleMissingCredentials() {
			if (this.credentialsNeeds("password") == true) {
				if (this.hasPassword() == false) {
					Console console = System.console();
					if (console != null) {
						String password = String.copyValueOf(console.readPassword("Password:"));
						this.putPasswordAtCredentials(password);
					}
				}
			}
		}

		protected boolean credentialsNeeds(String nameCredential) {
			Credential[] credential = getPluginCredentialsByTokenType(this);
			if (credential != null) {
				for (Credential cred : credential) {
					if (cred.getName() != null) {
						if (cred.getName().equals(nameCredential)) {
							return true;
						}
					}
				}
			}
			return false;
		}
	}

	@Parameters(separators = "=", commandDescription = "OCCI resources")
	private static class ResourceCommand extends AuthedCommand {
		@Parameter(names = "--get", description = "Get all resources")
		Boolean get = false;
	}

	private static class OCCIElement {

		private String term;
		private String scheme;

		private OCCIElement(String scheme, String term) {
			this.term = term;
			this.scheme = scheme;
		}

		public static OCCIElement createOCCIEl(String occiElStr) {
			int hashIndex = occiElStr.indexOf('#');
			return new OCCIElement(occiElStr.substring(0, hashIndex + 1), occiElStr.substring(hashIndex + 1));
		}

		public String getScheme() {
			return this.scheme;
		}

		public String getTerm() {
			return this.term;
		}
	}

}