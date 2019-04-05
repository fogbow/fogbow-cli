package cloud.fogbow.cli.ms.member;

import java.io.IOException;
import java.util.HashMap;

import cloud.fogbow.cli.HttpCliConstants;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.connectivity.HttpRequestClient;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import org.apache.http.ParseException;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import cloud.fogbow.cli.constants.Messages;

public class MemberCommand {
	public static final String NAME = "member";
	public static final String ENDPOINT = '/' + "members";

	@Parameter(names = CliCommonParameters.GET_ALL_COMMAND_KEY, description = Documentation.Member.GET_ALL)
	protected Boolean isGetAllCommand = false;

	@Parameter(names = CliCommonParameters.URL_COMMAND_KEY, description = Documentation.CommonParameters.URL, required = true)
	protected String url = null;

	public String run() throws FogbowException {
		if (this.url == null || this.url.isEmpty()) {
			throw new ParameterException(Messages.Exception.NO_MEMBERSHIP_SERVICE_URL);
		}

		return doGetAll();
	}
	
	private String doGetAll() throws FogbowException {
		String fullUrl = this.url + "/" + ENDPOINT;

		HashMap headers = new HashMap<>();
		HashMap body = new HashMap<>();

		HttpResponse httpResponse = HttpRequestClient.doGenericRequest(HttpMethod.GET, fullUrl, headers, body);
		return  httpResponse.getContent();
	}
}
