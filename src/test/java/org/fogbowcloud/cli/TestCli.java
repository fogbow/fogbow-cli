package org.fogbowcloud.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;
import org.fogbowcloud.cli.Main.TokenCommand;
import org.fogbowcloud.manager.core.plugins.IdentityPlugin;
import org.fogbowcloud.manager.core.plugins.util.Credential;
import org.fogbowcloud.manager.occi.OCCIConstants;
import org.fogbowcloud.manager.occi.model.ErrorType;
import org.fogbowcloud.manager.occi.model.HeaderUtils;
import org.fogbowcloud.manager.occi.model.OCCIException;
import org.fogbowcloud.manager.occi.model.OCCIHeaders;
import org.fogbowcloud.manager.occi.model.Token;
import org.fogbowcloud.manager.occi.order.OrderAttribute;
import org.fogbowcloud.manager.occi.order.OrderConstants;
import org.fogbowcloud.manager.occi.storage.StorageAttribute;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;

public class TestCli {

	private final String ORDER_ID = "234GD0-43254435-4543T4";
	private static final String ACCESS_TOKEN_ID = "accesstoken";
	private static final String INSTANCE_ID = "instanceid";

	private Main cli;
	private HttpClient client;
	private HttpUriRequestMatcher expectedRequest;

	@SuppressWarnings("static-access")
	@Before
	public void setUp() throws Exception {
		cli = new Main();
		client = Mockito.mock(HttpClient.class);
		HttpResponseFactory factory = new DefaultHttpResponseFactory();
		HttpResponse response = factory.newHttpResponse(
				new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_NO_CONTENT, "Return Irrelevant"), null);
		Mockito.when(client.execute(Mockito.any(HttpUriRequest.class))).thenReturn(response);
		cli.setClient(client);

	}

	@SuppressWarnings({ "static-access", "unchecked" })
	@Test
	public void commandGetToken() throws Exception {
		IdentityPlugin identityPlugin = Mockito.mock(IdentityPlugin.class);
		String accessId = "AccessId";
		Token token = new Token(accessId, new Token.User("user", "user"), null, null);
		Mockito.when(identityPlugin.createToken(Mockito.anyMap())).thenReturn(token);

		TokenCommand tokenCommand = new TokenCommand();
		tokenCommand.type = "openstack";
		tokenCommand.credentials = new HashMap<String, String>();

		cli.setIdentityPlugin(identityPlugin);
		Assert.assertEquals(accessId, cli.createToken(tokenCommand));
	}
	
	@SuppressWarnings({ "static-access", "unchecked" })
	@Test
	public void OCCIExceptionOnCommandGetToken() {

		IdentityPlugin identityPlugin = Mockito.mock(IdentityPlugin.class);
		cli.setIdentityPlugin(identityPlugin);

		Reflections reflections = new Reflections(ClasspathHelper.forPackage(Main.PLUGIN_PACKAGE),
				new SubTypesScanner());

		Set<Class<? extends IdentityPlugin>> allClasses = reflections.getSubTypesOf(IdentityPlugin.class);

		String pluginCredentialsInformation = cli.getPluginCredentialsInformation(allClasses);

		String msgException = "Erro while trying to sign the token";

		String response = msgException + System.lineSeparator() + pluginCredentialsInformation;

		Mockito.when(identityPlugin.createToken(Mockito.anyMap()))
				.thenThrow(new OCCIException(ErrorType.INTERNAL_SERVER_ERROR, msgException));

		TokenCommand tokenCommand = new TokenCommand();
		tokenCommand.type = "openstack";
		tokenCommand.credentials = new HashMap<String, String>();

		Assert.assertEquals(response, cli.createToken(tokenCommand));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandWithoutUrl() throws Exception {
		HttpUriRequest request = new HttpGet(Main.DEFAULT_URL + "/compute/");
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "instance --get --auth-token " + ACCESS_TOKEN_ID;
		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandPostOrder() throws Exception {
		final String intanceCount = "2";
		final String image = "image";
		final String flavor = "flavor";
		final String requirements = "X>=1&&Y=2";

		HttpUriRequest request = new HttpPost(Main.DEFAULT_URL + "/" + OrderConstants.TERM);
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader("Category",
				OrderConstants.TERM + "; scheme=\"" + OrderConstants.SCHEME + "\"; class=\"kind\"");
		request.addHeader("X-OCCI-Attribute", OrderAttribute.INSTANCE_COUNT.getValue() + "=" + intanceCount);
		request.addHeader("X-OCCI-Attribute", OrderAttribute.TYPE.getValue() + "=one-time");
		request.addHeader("X-OCCI-Attribute", OrderAttribute.REQUIREMENTS.getValue() + "=" + requirements);
		request.addHeader("Category",
				flavor + "; scheme=\"http://schemas.fogbowcloud.org/template/resource#\"; class=\"mixin\"");
		request.addHeader("Category",
				image + "; scheme=\"http://schemas.fogbowcloud.org/template/os#\"; class=\"mixin\"");
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		request.addHeader("X-OCCI-Attribute",
				OrderAttribute.RESOURCE_KIND.getValue() + "=" + OrderConstants.COMPUTE_TERM);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "order --create --n " + intanceCount + " --url " + Main.DEFAULT_URL + " " + "--image " + image
				+ " --auth-token " + ACCESS_TOKEN_ID + " --requirements " + requirements + " --flavor " + flavor
				+ " --resource-kind " + OrderConstants.COMPUTE_TERM;

		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandPostOrderWithoutResourceKind() throws Exception {
		final String intanceCount = "2";
		final String requirements = "X>=1&&Y=2";

		HttpUriRequest request = new HttpPost(Main.DEFAULT_URL + "/" + OrderConstants.TERM);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "order --create --n " + intanceCount + " --url " + Main.DEFAULT_URL + " " + "--image "
				+ "image" + " --auth-token " + ACCESS_TOKEN_ID + " --requirements " + requirements + " --flavor "
				+ "flavor";

		cli.main(createArgs(command));

		Mockito.verify(client, Mockito.times(0)).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandPostOrderWithResourceKindStorage() throws Exception {
		final String intanceCount = "2";
		final String requirements = "X>=1&&Y=2";
		final String size = "20";

		HttpUriRequest request = new HttpPost(Main.DEFAULT_URL + "/" + OrderConstants.TERM);
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader("Category",
				OrderConstants.TERM + "; scheme=\"" + OrderConstants.SCHEME + "\"; class=\"kind\"");
		request.addHeader("X-OCCI-Attribute", OrderAttribute.INSTANCE_COUNT.getValue() + "=" + intanceCount);
		request.addHeader("X-OCCI-Attribute", OrderAttribute.TYPE.getValue() + "=one-time");
		request.addHeader("X-OCCI-Attribute", OrderAttribute.REQUIREMENTS.getValue() + "=" + requirements);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		request.addHeader("X-OCCI-Attribute", OrderAttribute.STORAGE_SIZE.getValue() + "=" + size);
		request.addHeader("X-OCCI-Attribute",
				OrderAttribute.RESOURCE_KIND.getValue() + "=" + OrderConstants.STORAGE_TERM);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "order --create --n " + intanceCount + " --url " + Main.DEFAULT_URL + " " + " --auth-token "
				+ ACCESS_TOKEN_ID + " --requirements " + requirements + " --resource-kind "
				+ OrderConstants.STORAGE_TERM + " --size " + size;

		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandPostOrderWithResourceKindNetwork() throws Exception {
		final String intanceCount = "2";
		final String requirements = "X>=1&&Y=2";
		String cird = "10.10.10.10/24";
		String allocation = OCCIConstants.NetworkAllocation.DYNAMIC.getValue();
		String gateway = "10.10.10.11";

		HttpUriRequest request = new HttpPost(Main.DEFAULT_URL + "/" + OrderConstants.TERM);
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader("Category",
				OrderConstants.TERM + "; scheme=\"" + OrderConstants.SCHEME + "\"; class=\"kind\"");
		request.addHeader("X-OCCI-Attribute", OrderAttribute.INSTANCE_COUNT.getValue() + "=" + intanceCount);
		request.addHeader("X-OCCI-Attribute", OrderAttribute.TYPE.getValue() + "=one-time");
		request.addHeader("X-OCCI-Attribute", OrderAttribute.REQUIREMENTS.getValue() + "=" + requirements);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		request.addHeader("X-OCCI-Attribute", OCCIConstants.NETWORK_GATEWAY + "=" + gateway);
		request.addHeader("X-OCCI-Attribute", OCCIConstants.NETWORK_ALLOCATION + "=" + allocation);
		request.addHeader("X-OCCI-Attribute", OCCIConstants.NETWORK_ADDRESS + "=" + cird);
		request.addHeader("X-OCCI-Attribute",
				OrderAttribute.RESOURCE_KIND.getValue() + "=" + OrderConstants.NETWORK_TERM);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "order --create --n " + intanceCount + " --url " + Main.DEFAULT_URL + " " + " --auth-token "
				+ ACCESS_TOKEN_ID + " --requirements " + requirements + " --resource-kind "
				+ OrderConstants.NETWORK_TERM + " --cidr " + cird + " --gateway " + gateway + " --allocation "
				+ OCCIConstants.NetworkAllocation.DYNAMIC.getValue();

		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandPostOrderWithResourceKindNetworkWithoutAttributes() throws Exception {
		final String intanceCount = "2";
		final String requirements = "X>=1&&Y=2";

		HttpUriRequest request = new HttpPost(Main.DEFAULT_URL + "/" + OrderConstants.TERM);
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader("Category",
				OrderConstants.TERM + "; scheme=\"" + OrderConstants.SCHEME + "\"; class=\"kind\"");
		request.addHeader("X-OCCI-Attribute", OrderAttribute.INSTANCE_COUNT.getValue() + "=" + intanceCount);
		request.addHeader("X-OCCI-Attribute", OrderAttribute.TYPE.getValue() + "=one-time");
		request.addHeader("X-OCCI-Attribute", OrderAttribute.REQUIREMENTS.getValue() + "=" + requirements);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		request.addHeader("X-OCCI-Attribute",
				OrderAttribute.RESOURCE_KIND.getValue() + "=" + OrderConstants.NETWORK_TERM);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "order --create --n " + intanceCount + " --url " + Main.DEFAULT_URL + " " + " --auth-token "
				+ ACCESS_TOKEN_ID + " --requirements " + requirements + " --resource-kind "
				+ OrderConstants.NETWORK_TERM;

		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandPostOrderWithResourceKindNetworkWithWrongAllocation() throws Exception {
		final String intanceCount = "2";
		final String requirements = "X>=1&&Y=2";
		String cird = "10.10.10.10/24";
		String allocation = OCCIConstants.NetworkAllocation.DYNAMIC.getValue();
		String gateway = "10.10.10.11";

		HttpUriRequest request = new HttpPost(Main.DEFAULT_URL + "/" + OrderConstants.TERM);
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader("Category",
				OrderConstants.TERM + "; scheme=\"" + OrderConstants.SCHEME + "\"; class=\"kind\"");
		request.addHeader("X-OCCI-Attribute", OrderAttribute.INSTANCE_COUNT.getValue() + "=" + intanceCount);
		request.addHeader("X-OCCI-Attribute", OrderAttribute.TYPE.getValue() + "=one-time");
		request.addHeader("X-OCCI-Attribute", OrderAttribute.REQUIREMENTS.getValue() + "=" + requirements);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		request.addHeader("X-OCCI-Attribute", OCCIConstants.NETWORK_GATEWAY + "=" + gateway);
		request.addHeader("X-OCCI-Attribute", OCCIConstants.NETWORK_ALLOCATION + "=" + allocation);
		request.addHeader("X-OCCI-Attribute", OCCIConstants.NETWORK_ADDRESS + "=" + cird);
		request.addHeader("X-OCCI-Attribute",
				OrderAttribute.RESOURCE_KIND.getValue() + "=" + OrderConstants.NETWORK_TERM);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "order --create --n " + intanceCount + " --url " + Main.DEFAULT_URL + " " + " --auth-token "
				+ ACCESS_TOKEN_ID + " --requirements " + requirements + " --resource-kind "
				+ OrderConstants.NETWORK_TERM + " --allocation " + "wrong";

		cli.main(createArgs(command));

		Mockito.verify(client, Mockito.times(0)).execute(Mockito.any(HttpUriRequest.class));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandPostOrderWithResourceKindStorageWithoutSizeAttribute() throws Exception {
		final String intanceCount = "2";
		final String requirements = "X>=1&&Y=2";

		HttpUriRequest request = new HttpPost(Main.DEFAULT_URL + "/" + OrderConstants.TERM);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "order --create --n " + intanceCount + " --url " + Main.DEFAULT_URL + " " + " --auth-token "
				+ ACCESS_TOKEN_ID + " --requirements " + requirements + " --resource-kind "
				+ OrderConstants.STORAGE_TERM;

		cli.main(createArgs(command));

		Mockito.verify(client, Mockito.times(0)).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandPostOrderWithDataUser() throws Exception {
		final String intanceCount = "2";
		final String image = "image";
		final String flavor = "flavor";
		final String requirements = "X>=1&&Y=2";
		String userDataPath = "src/test/resource/get_content";
		String userDataContent = "";
		File file = new File(userDataPath);
		userDataContent = IOUtils.toString(new FileInputStream(file));
		String type = "Maaarcos";

		HttpUriRequest request = new HttpPost(Main.DEFAULT_URL + "/" + OrderConstants.TERM);
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader("Category",
				OrderConstants.TERM + "; scheme=\"" + OrderConstants.SCHEME + "\"; class=\"kind\"");
		request.addHeader("X-OCCI-Attribute", OrderAttribute.INSTANCE_COUNT.getValue() + "=" + intanceCount);
		request.addHeader("X-OCCI-Attribute", OrderAttribute.TYPE.getValue() + "=one-time");
		request.addHeader("X-OCCI-Attribute", OrderAttribute.REQUIREMENTS.getValue() + "=" + requirements);
		request.addHeader("X-OCCI-Attribute", OrderAttribute.EXTRA_USER_DATA_ATT.getValue() + "="
				+ new String(Base64.encodeBase64(userDataContent.getBytes())));
		request.addHeader("X-OCCI-Attribute", OrderAttribute.EXTRA_USER_DATA_CONTENT_TYPE_ATT.getValue() + "=" + type);
		request.addHeader("Category",
				flavor + "; scheme=\"http://schemas.fogbowcloud.org/template/resource#\"; class=\"mixin\"");
		request.addHeader("Category",
				image + "; scheme=\"http://schemas.fogbowcloud.org/template/os#\"; class=\"mixin\"");
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		request.addHeader("X-OCCI-Attribute",
				OrderAttribute.RESOURCE_KIND.getValue() + "=" + OrderConstants.COMPUTE_TERM);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "order --create --n " + intanceCount + " --url " + Main.DEFAULT_URL + " " + "--image " + image
				+ " --auth-token " + ACCESS_TOKEN_ID + " --requirements " + requirements + " --flavor " + flavor
				+ " --user-data-file " + userDataPath + " --user-data-file-content-type " + type
				+ " --resource-kind compute";

		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandPostOrderWithoutRequirements() throws Exception {
		final String intanceCount = "2";
		final String image = "image";
		final String flavor = "flavor";

		HttpUriRequest request = new HttpPost(Main.DEFAULT_URL + "/" + OrderConstants.TERM);
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader("Category",
				OrderConstants.TERM + "; scheme=\"" + OrderConstants.SCHEME + "\"; class=\"kind\"");
		request.addHeader("X-OCCI-Attribute", OrderAttribute.INSTANCE_COUNT.getValue() + "=" + intanceCount);
		request.addHeader("X-OCCI-Attribute", OrderAttribute.TYPE.getValue() + "=one-time");
		request.addHeader("Category",
				flavor + "; scheme=\"http://schemas.fogbowcloud.org/template/resource#\"; class=\"mixin\"");
		request.addHeader("Category",
				image + "; scheme=\"http://schemas.fogbowcloud.org/template/os#\"; class=\"mixin\"");
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		request.addHeader("X-OCCI-Attribute",
				OrderAttribute.RESOURCE_KIND.getValue() + "=" + OrderConstants.COMPUTE_TERM);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "order --create --n " + intanceCount + " --url " + Main.DEFAULT_URL + " " + "--image " + image
				+ " --auth-token " + ACCESS_TOKEN_ID + " --flavor " + flavor + " --resource-kind compute";
		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandPostOrderDefaultValues() throws Exception {
		String requirements = "X1==&&Y==2";

		HttpUriRequest request = new HttpPost(Main.DEFAULT_URL + "/" + OrderConstants.TERM);
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader("Category", OrderConstants.TERM + "; scheme=\"" + OrderConstants.SCHEME + "\"; class=\""
				+ OrderConstants.KIND_CLASS + "\"");
		request.addHeader("X-OCCI-Attribute",
				OrderAttribute.INSTANCE_COUNT.getValue() + "=" + Main.DEFAULT_INTANCE_COUNT);
		request.addHeader("X-OCCI-Attribute", OrderAttribute.TYPE.getValue() + "=one-time");
		request.addHeader("X-OCCI-Attribute", OrderAttribute.REQUIREMENTS.getValue() + "=" + requirements);
		request.addHeader("Category", Main.DEFAULT_IMAGE + "; scheme=\"" + OrderConstants.TEMPLATE_OS_SCHEME
				+ "\"; class=\"" + OrderConstants.MIXIN_CLASS + "\"");
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		request.addHeader("X-OCCI-Attribute",
				OrderAttribute.RESOURCE_KIND.getValue() + "=" + OrderConstants.COMPUTE_TERM);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "order --create --url " + Main.DEFAULT_URL + " --auth-token " + ACCESS_TOKEN_ID
				+ " --requirements " + requirements + " --resource-kind compute";

		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandGetSpecificOrder() throws Exception {
		HttpUriRequest request = new HttpGet(Main.DEFAULT_URL + "/" + OrderConstants.TERM + "/" + ORDER_ID);
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);

		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "order --get --url " + Main.DEFAULT_URL + " --auth-token " + ACCESS_TOKEN_ID + " --id "
				+ ORDER_ID;
		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandGetOrder() throws Exception {
		HttpUriRequest request = new HttpGet(Main.DEFAULT_URL + "/" + OrderConstants.TERM);
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);

		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "order --get --url " + Main.DEFAULT_URL + " --auth-token " + ACCESS_TOKEN_ID;
		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandDeleteOrder() throws Exception {
		HttpUriRequest request = new HttpDelete(Main.DEFAULT_URL + "/" + OrderConstants.TERM + "/" + ORDER_ID);
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);

		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "order --delete --url " + Main.DEFAULT_URL + " --auth-token " + ACCESS_TOKEN_ID + " --id "
				+ ORDER_ID;
		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandGetQuery() throws Exception {
		HttpUriRequest request = new HttpGet(Main.DEFAULT_URL + "/-/");
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "resource --get";
		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandGetMembers() throws Exception {
		HttpUriRequest request = new HttpGet(Main.DEFAULT_URL + "/member");
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "member --auth-token " + ACCESS_TOKEN_ID;
		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandGetMemberQuota() throws Exception {
		String id = "id";
		HttpUriRequest request = new HttpGet(Main.DEFAULT_URL + "/member/" + id + "/quota");
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "member --quota --id " + id + " --auth-token " + ACCESS_TOKEN_ID;
		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandCreateInstance() throws Exception {
		HttpUriRequest request = new HttpPost(Main.DEFAULT_URL + "/compute/");
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		request.addHeader("Category", OrderConstants.COMPUTE_TERM + "; scheme=\""
				+ OrderConstants.INFRASTRUCTURE_OCCI_SCHEME + "\"; class=\"" + OrderConstants.KIND_CLASS + "\"");
		request.addHeader("Category", "large; scheme=\"http://schemas.openstack.org/template/resource#\"; class=\""
				+ OrderConstants.MIXIN_CLASS + "\"");
		request.addHeader("Category", "imageName; scheme=\"http://schemas.openstack.org/template/os#\"; class=\""
				+ OrderConstants.MIXIN_CLASS + "\"");

		expectedRequest = new HttpUriRequestMatcher(request);

		String flavorId = "http://schemas.openstack.org/template/resource#large";
		String imageId = "http://schemas.openstack.org/template/os#imageName";

		String command = "instance --create --url " + Main.DEFAULT_URL + " " + " --auth-token " + ACCESS_TOKEN_ID
				+ " --image " + imageId + " --flavor " + flavorId;

		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandCreateInstanceWithUserData() throws Exception {
		String userDataPath = "src/test/resource/get_content";
		String userDataContent = "";
		File file = new File(userDataPath);
		userDataContent = IOUtils.toString(new FileInputStream(file));

		HttpUriRequest request = new HttpPost(Main.DEFAULT_URL + "/compute/");
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		request.addHeader("Category", OrderConstants.COMPUTE_TERM + "; scheme=\""
				+ OrderConstants.INFRASTRUCTURE_OCCI_SCHEME + "\"; class=\"" + OrderConstants.KIND_CLASS + "\"");
		request.addHeader("Category", "large; scheme=\"http://schemas.openstack.org/template/resource#\"; class=\""
				+ OrderConstants.MIXIN_CLASS + "\"");
		request.addHeader("Category", "imageName; scheme=\"http://schemas.openstack.org/template/os#\"; class=\""
				+ OrderConstants.MIXIN_CLASS + "\"");
		request.addHeader("Category", "user_data" + "; scheme=\"" + "http://schemas.openstack.org/compute/instance#"
				+ "\"; class=\"" + OrderConstants.MIXIN_CLASS + "\"");
		request.addHeader("X-OCCI-Attribute",
				"org.openstack.compute.user_data=" + new String(Base64.encodeBase64(userDataContent.getBytes())));

		expectedRequest = new HttpUriRequestMatcher(request);

		String flavorId = "http://schemas.openstack.org/template/resource#large";
		String imageId = "http://schemas.openstack.org/template/os#imageName";

		String command = "instance --create --url " + Main.DEFAULT_URL + " " + " --auth-token " + ACCESS_TOKEN_ID
				+ " --image " + imageId + " --flavor " + flavorId + " --user-data-file " + userDataPath;

		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandGetInstance() throws Exception {
		HttpUriRequest request = new HttpGet(Main.DEFAULT_URL + "/compute/");
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "instance --get --url " + Main.DEFAULT_URL + " " + " --auth-token " + ACCESS_TOKEN_ID;
		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandGetSpecificInstance() throws Exception {
		HttpUriRequest request = new HttpGet(Main.DEFAULT_URL + "/compute/" + INSTANCE_ID);
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "instance --get --url " + Main.DEFAULT_URL + " " + "--id " + INSTANCE_ID + " --auth-token "
				+ ACCESS_TOKEN_ID;
		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandDeleteInstance() throws Exception {
		HttpUriRequest request = new HttpDelete(Main.DEFAULT_URL + "/compute/" + INSTANCE_ID);
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "instance --delete --url " + Main.DEFAULT_URL + " " + "--id " + INSTANCE_ID + " --auth-token "
				+ ACCESS_TOKEN_ID;

		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void testGetCredentialsInformation() {
		Reflections reflections = new Reflections(ClasspathHelper.forPackage(Main.PLUGIN_PACKAGE),
				new SubTypesScanner());

		Set<Class<? extends IdentityPlugin>> allClasses = reflections.getSubTypesOf(IdentityPlugin.class);

		String response = cli.getPluginCredentialsInformation(allClasses);

		for (Class<? extends IdentityPlugin> eachClass : allClasses) {
			IdentityPlugin identityPlugin = null;
			try {
				identityPlugin = (IdentityPlugin) cli.createInstance(eachClass, new Properties());
				for (Credential credential : identityPlugin.getCredentials()) {
					Assert.assertTrue(response.contains(credential.getName()));
					if (credential.getValueDefault() != null) {
						Assert.assertTrue(response.contains(credential.getValueDefault()));
					}
				}
			} catch (Exception e) {
			}
		}

	}

	@Test
	public void testGetLocationHeader() {
		Header[] headers = new Header[3];
		String responseOk = "OK";
		headers[0] = new BasicHeader("Location", responseOk);
		headers[1] = new BasicHeader("Test1", "");
		headers[2] = new BasicHeader("Test2", "");
		Header locationHeader = Main.getLocationHeader(headers);
		Assert.assertEquals(responseOk, locationHeader.getValue());
	}

	@Test
	public void testGenerateLocationHeaderResponse() {
		Header[] headers = new Header[2];
		String value1 = "value1";
		String value2 = "value2";
		String valueLocationHeader = value1 + "," + value2;
		headers[0] = new BasicHeader("Location", valueLocationHeader);
		headers[1] = new BasicHeader("Test1", "");
		Header locationHeader = Main.getLocationHeader(headers);
		String response = Main.generateLocationHeaderResponse(locationHeader);
		String correctResponse = HeaderUtils.X_OCCI_LOCATION_PREFIX + value1 + "\n" + HeaderUtils.X_OCCI_LOCATION_PREFIX
				+ value2;
		Assert.assertEquals(correctResponse, response);
	}

	@SuppressWarnings("static-access")
	@Test
	public void TestNormalizeToken() throws FileNotFoundException, IOException {
		String token = "Test\nSpace";
		String tokenNormalized = cli.normalizeToken(token);
		Assert.assertEquals(token.replace("\n", ""), tokenNormalized);

		File file = new File("src/test/resource/get_content");
		token = IOUtils.toString(new FileInputStream(file));
		Assert.assertTrue(token.length() > 0);

		file = new File("src/test/resource/wrong");
		try {
			token = IOUtils.toString(new FileInputStream(file));
		} catch (Exception e) {
			token = null;
		}
		Assert.assertNull(token);
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandPostAttachment() throws Exception {
		final String source = "source";
		final String target = "target";
		final String deviceId = "deviceId";

		HttpUriRequest request = new HttpPost(
				Main.DEFAULT_URL + "/" + OrderConstants.STORAGE_TERM + "/" + OrderConstants.STORAGE_LINK_TERM + "/");
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		request.addHeader("Category", OrderConstants.STORAGELINK_TERM + "; scheme=\""
				+ OrderConstants.INFRASTRUCTURE_OCCI_SCHEME + "\"; class=\"" + OrderConstants.KIND_CLASS + "\"");
		request.addHeader("X-OCCI-Attribute", StorageAttribute.SOURCE.getValue() + "=" + source);
		request.addHeader("X-OCCI-Attribute", StorageAttribute.DEVICE_ID.getValue() + "=" + deviceId);
		request.addHeader("X-OCCI-Attribute", StorageAttribute.TARGET.getValue() + "=" + target);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "attachment --create --url " + Main.DEFAULT_URL + " " + "--computeId " + source
				+ " --auth-token " + ACCESS_TOKEN_ID + " --storageId " + target + " --mountPoint " + deviceId;

		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandGetAttachment() throws Exception {
		HttpUriRequest request = new HttpGet(
				Main.DEFAULT_URL + "/" + OrderConstants.STORAGE_TERM + "/" + OrderConstants.STORAGE_LINK_TERM + "/");
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "attachment --get --url " + Main.DEFAULT_URL + " " + " --auth-token " + ACCESS_TOKEN_ID;

		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandGetSpecificAttachment() throws Exception {
		String storageLinkId = "storageLinkId";
		HttpUriRequest request = new HttpGet(Main.DEFAULT_URL + "/" + OrderConstants.STORAGE_TERM + "/"
				+ OrderConstants.STORAGE_LINK_TERM + "/" + storageLinkId);
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "attachment --get --url " + Main.DEFAULT_URL + " " + " --auth-token " + ACCESS_TOKEN_ID
				+ " --id " + storageLinkId;

		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandDeleteSpecificAttachment() throws Exception {
		String storageLinkId = "storageLinkId";
		HttpUriRequest request = new HttpDelete(Main.DEFAULT_URL + "/" + OrderConstants.STORAGE_TERM + "/"
				+ OrderConstants.STORAGE_LINK_TERM + "/" + storageLinkId);
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "attachment --delete --url " + Main.DEFAULT_URL + " " + " --auth-token " + ACCESS_TOKEN_ID
				+ " --id " + storageLinkId;

		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void testGetTokenInfoOnlyResponseWithoutAttributesOnCommand() {
		TokenCommand tokenCommand = new TokenCommand();

		IdentityPlugin identityPlugin = Mockito.mock(IdentityPlugin.class);

		HashMap<String, String> attributes = new HashMap<String, String>();
		Token token = new Token("accessId", new Token.User("user", "user"), new Date(), attributes);
		Mockito.when(identityPlugin.getToken(Mockito.anyString())).thenReturn(token);
		cli.setIdentityPlugin(identityPlugin);

		String responseStr = cli.getTokenInfo(tokenCommand);
		Assert.assertEquals(token.toString(), responseStr);
	}

	@SuppressWarnings("static-access")
	@Test
	public void testGetTokenInfoOnlyResponseWithAttributeUserName() {
		TokenCommand tokenCommand = new TokenCommand();
		tokenCommand.type = "openstack";
		tokenCommand.userName = true;

		IdentityPlugin identityPlugin = Mockito.mock(IdentityPlugin.class);

		HashMap<String, String> attributes = new HashMap<String, String>();
		String user = "user";
		Token token = new Token("accessId", new Token.User("userId", user), new Date(), attributes);
		Mockito.when(identityPlugin.getToken(Mockito.anyString())).thenReturn(token);
		cli.setIdentityPlugin(identityPlugin);

		String responseStr = cli.getTokenInfo(tokenCommand);
		Assert.assertEquals(user, responseStr);
	}

	@SuppressWarnings("static-access")
	@Test
	public void testGetTokenInfoOnlyResponseWithAttributeUserId() {
		TokenCommand tokenCommand = new TokenCommand();
		tokenCommand.type = "openstack";
		tokenCommand.userId = true;

		IdentityPlugin identityPlugin = Mockito.mock(IdentityPlugin.class);

		HashMap<String, String> attributes = new HashMap<String, String>();
		String username = "user";
		String userId = "userId";
		Token token = new Token("accessId", new Token.User(userId, username), new Date(), attributes);
		Mockito.when(identityPlugin.getToken(Mockito.anyString())).thenReturn(token);
		cli.setIdentityPlugin(identityPlugin);

		String responseStr = cli.getTokenInfo(tokenCommand);
		Assert.assertEquals(userId, responseStr);
	}

	@SuppressWarnings("static-access")
	@Test
	public void testGetTokenInfoOnlyResponseWithAttributeFullUser() {
		TokenCommand tokenCommand = new TokenCommand();
		tokenCommand.type = "openstack";
		tokenCommand.userId = true;
		tokenCommand.userName = true;

		IdentityPlugin identityPlugin = Mockito.mock(IdentityPlugin.class);

		HashMap<String, String> attributes = new HashMap<String, String>();
		String username = "user";
		String userId = "userId";
		Token token = new Token("accessId", new Token.User(userId, username), new Date(), attributes);
		Mockito.when(identityPlugin.getToken(Mockito.anyString())).thenReturn(token);
		cli.setIdentityPlugin(identityPlugin);

		String responseStr = cli.getTokenInfo(tokenCommand);
		Assert.assertEquals(username + "," + userId, responseStr);
	}

	@SuppressWarnings("static-access")
	@Test
	public void testGetTokenInfoOnlyResponseWithAttributeAccessId() {
		TokenCommand tokenCommand = new TokenCommand();
		tokenCommand.type = "openstack";
		tokenCommand.accessId = true;

		IdentityPlugin identityPlugin = Mockito.mock(IdentityPlugin.class);

		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("x", "y");
		String user = "user";
		String accessId = "accessId";
		Token token = new Token(accessId, new Token.User(user, user), new Date(), attributes);
		Mockito.when(identityPlugin.getToken(Mockito.anyString())).thenReturn(token);
		cli.setIdentityPlugin(identityPlugin);

		String responseStr = cli.getTokenInfo(tokenCommand);
		Assert.assertEquals(accessId, responseStr);
	}

	@SuppressWarnings("static-access")
	@Test
	public void testGetTokenInfoOnlyResponseWithAttributeAccessIdAndUser() {
		TokenCommand tokenCommand = new TokenCommand();
		tokenCommand.type = "openstack";
		tokenCommand.accessId = true;
		tokenCommand.userName = true;

		IdentityPlugin identityPlugin = Mockito.mock(IdentityPlugin.class);

		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("x", "y");
		String user = "user";
		String accessId = "accessId";
		Token token = new Token(accessId, new Token.User(user, user), new Date(), attributes);
		Mockito.when(identityPlugin.getToken(Mockito.anyString())).thenReturn(token);
		cli.setIdentityPlugin(identityPlugin);

		String responseStr = cli.getTokenInfo(tokenCommand);
		Assert.assertEquals(accessId + "," + user, responseStr);
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandGetNetworks() throws Exception {
		HttpUriRequest request = new HttpGet(Main.DEFAULT_URL + "/" + OrderConstants.NETWORK_TERM + "/");
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "network --get --auth-token " + ACCESS_TOKEN_ID;
		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandGetSpecificNetwork() throws Exception {
		String networkId = "networkId00";
		HttpUriRequest request = new HttpGet(Main.DEFAULT_URL + "/" + OrderConstants.NETWORK_TERM + "/" + networkId);
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "network --get --auth-token " + ACCESS_TOKEN_ID + " --id " + networkId;
		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void commandDeleteSpecificNetwork() throws Exception {
		String networkId = "networkId00";
		HttpUriRequest request = new HttpDelete(Main.DEFAULT_URL + "/" + OrderConstants.NETWORK_TERM + "/" + networkId);
		request.addHeader(OCCIHeaders.CONTENT_TYPE, OCCIHeaders.OCCI_CONTENT_TYPE);
		request.addHeader(OCCIHeaders.X_AUTH_TOKEN, ACCESS_TOKEN_ID);
		expectedRequest = new HttpUriRequestMatcher(request);

		String command = "network --delete --auth-token " + ACCESS_TOKEN_ID + " --id " + networkId;
		cli.main(createArgs(command));

		Mockito.verify(client).execute(Mockito.argThat(expectedRequest));
	}

	@Test
	public void testTokenCommandCredentialNeeds() throws Exception {

		TokenCommand token = new TokenCommand();

		LinkedList<String> types = (LinkedList<String>) Main.getNamePossiblePluginTypes();

		ArrayList<String> typesNeedPass = new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("openstackv2");
				add("ldap");
				add("opennebula");
				add("openstack");
				add("voms");
			}
		};

		for (String type : types) {
			token.type = type;
			if (typesNeedPass.contains(type))
				Assert.assertEquals(true, token.credentialsNeeds("password"));
			else
				Assert.assertEquals(false, token.credentialsNeeds("password"));
		}

		Assert.assertEquals(false, token.hasPassword());

		token.putPasswordAtCredentials("teste");

		Assert.assertEquals(true, token.hasPassword());
	}

	private String[] createArgs(String command) throws Exception {
		return command.trim().split(" ");
	}

	private class HttpUriRequestMatcher extends ArgumentMatcher<HttpUriRequest> {

		private HttpUriRequest request;

		public HttpUriRequestMatcher(HttpUriRequest request) {
			this.request = request;
		}

		public boolean matches(Object object) {

			HttpUriRequest comparedRequest = (HttpUriRequest) object;
			if (!this.request.getURI().equals(comparedRequest.getURI())) {
				return false;
			}
			if (!checkHeaders(comparedRequest.getAllHeaders())) {
				return false;
			}
			if (!this.request.getMethod().equals(comparedRequest.getMethod())) {
				return false;
			}
			return true;
		}

		public boolean checkHeaders(Header[] comparedHeaders) {
			for (Header comparedHeader : comparedHeaders) {
				boolean headerEquals = false;
				for (Header header : this.request.getAllHeaders()) {
					if (header.getName().equals(OCCIHeaders.X_AUTH_TOKEN)) {
						if (header.getName().equals(comparedHeader.getName())) {
							headerEquals = true;
							break;
						}
					} else if (header.getName().equals(comparedHeader.getName())
							&& header.getValue().equals(comparedHeader.getValue())) {
						headerEquals = true;
						continue;
					}
				}
				if (!headerEquals) {
					return false;
				}
			}
			return true;
		}
	}
}
