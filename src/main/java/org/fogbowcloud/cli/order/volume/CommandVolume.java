package org.fogbowcloud.cli.order.volume;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.HttpUtil;
import org.fogbowcloud.cli.order.OrderCommand;
import org.fogbowcloud.manager.api.http.VolumeOrdersController;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;
import com.google.gson.Gson;

public class CommandVolume extends OrderCommand {
	
	public static final String NAME = "volume";
	
	@ParametersDelegate
	private Volume volume = new Volume();
	
	public static final String ENDPOINT = '/' + VolumeOrdersController.VOLUME_ENDPOINT;
	
	public String run() throws ClientProtocolException, IOException {
		if (this.isCreateCommand) {
			return doCreateVolume();
		} else if (this.isDeleteCommand) {
			return doDeleteVolume();
		} else if (this.isGetCommand) {
			return doGetVolume();
		} else if (this.isGetAllCommand) {
			return doGetAllVolume();
		}
		throw new ParameterException("command is incomplete");
	}
	
	private String doCreateVolume() throws ClientProtocolException, IOException {
		String fullUrl = this.url + ENDPOINT;
		HttpResponse httpResponse = HttpUtil.post(fullUrl, volumeToJson(), this.federationToken);
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}

	private String doDeleteVolume() throws ClientProtocolException, IOException {
		String fullUrl = this.url + ENDPOINT + "/" + this.id;
		HttpResponse httpResponse = HttpUtil.delete(fullUrl, this.federationToken);
		return httpResponse.getStatusLine().toString();
	}

	private String doGetVolume() throws ClientProtocolException, IOException {
		String fullUrl = this.url + ENDPOINT + "/" + this.id;
		HttpResponse httpResponse = HttpUtil.get(fullUrl, this.federationToken);
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}
	
	private String doGetAllVolume() throws ClientProtocolException, IOException {
		String fullUrl = this.url + ENDPOINT;
		HttpResponse httpResponse = HttpUtil.get(fullUrl, this.federationToken);
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}
	
	protected String volumeToJson() {
		Gson gson = new Gson();
		String computeJson = gson.toJson(this.volume);
		return computeJson;
	}
}
