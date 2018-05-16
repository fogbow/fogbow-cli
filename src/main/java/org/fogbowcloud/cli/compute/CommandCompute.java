package org.fogbowcloud.cli.compute;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.google.gson.Gson;

@Parameters(separators = "=", commandDescription = "Compute manipulation")
public class CommandCompute {
	
	public static final String NAME = "compute";
	
	@Parameter(names = { "--create", "-c" }, description = "Creates a new compute")
	private Boolean isCreation = false;
	
	@Parameter(names = { "--token", "-t" }, description = "User's Token")
	private String token = null;
	
	@Parameter(names = { "--local-token", "-lt" }, description = "Local token")
	private String localToken = null;
	
	@Parameter(names = { "--url", "-url" }, description = "Url")
	private String url = null;
	
	@ParametersDelegate
	private Compute compute = new Compute(); 
	
	public String run() throws ClientProtocolException, IOException {
		if (isCreation) {
			return createCompute();
		}
		return null;
	}

	private String createCompute() throws ClientProtocolException, IOException {
		Gson gson = new Gson();
		String computeJson = gson.toJson(compute);
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		StringEntity postingString = new StringEntity(computeJson);
		post.setEntity(postingString);
		post.setHeader("acessid", token);
		/**
		 * 1 - Criar HttpClientBuilder.create().build() numa variável e mockar essa chamada para retornar a variável
		 * 2 - Mockar o metodo execute para retornar um response criado
		 */
		HttpResponse response = httpClient.execute(post);
		
		HttpEntity entity = response.getEntity();
		String responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);
		return responseString;
	}
}
