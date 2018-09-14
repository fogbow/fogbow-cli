package org.fogbowcloud.cli.member;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.fogbowcloud.cli.HttpUtil;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.fogbowcloud.cli.constants.Messages;

public class MemberCommand {
	
	public static final String NAME = "member";
	public static final String ENDPOINT = '/' + "members";
	
	public static final String GET_ALL_COMMAND_KEY = "--get-all";
	@Parameter(names = { GET_ALL_COMMAND_KEY }, description = "Get all computes")
	protected Boolean isGetAllCommand = false;
	
	public static final String URL_COMMAND_KEY =  "--url";
	@Parameter(names = { URL_COMMAND_KEY }, description = "Url for my membership service API", required = true)
	protected String url = null;

	public String run() throws IOException {
		if (this.url == null || this.url.isEmpty()) {
			throw new ParameterException(Messages.Exception.NO_MEMBERSHIP_SERVICE_URL_PASSED);
		}

		if (this.isGetAllCommand) {
			return doGetAll();
		}

		throw new ParameterException("command is incomplete");
	}
	
	private String doGetAll() throws ParseException, IOException {
		String fullUrl = this.url + ENDPOINT;
		HttpResponse httpResponse = HttpUtil.get(fullUrl);
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}
	
}
