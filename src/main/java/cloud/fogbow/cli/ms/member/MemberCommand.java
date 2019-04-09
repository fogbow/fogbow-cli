package cloud.fogbow.cli.ms.member;

import java.io.IOException;
import java.util.HashMap;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.HttpCliConstants;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.connectivity.HttpRequestClient;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import com.beust.jcommander.ParametersDelegate;
import org.apache.http.ParseException;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import cloud.fogbow.cli.constants.Messages;

public class MemberCommand {
	public static final String NAME = "member";
	public static final String ENDPOINT = '/' + "members";

	@Parameter(names = CliCommonParameters.GET_ALL_COMMAND_KEY, description = Documentation.Member.GET_ALL)
	private Boolean isGetAllCommand = false;

	@ParametersDelegate
	private FogbowCliHttpUtil fogbowCliHttpUtil = new FogbowCliHttpUtil();

	public String run() throws FogbowException {
		String url = fogbowCliHttpUtil.getUrl();

		if (url == null || url.isEmpty()) {
			throw new ParameterException(Messages.Exception.NO_MEMBERSHIP_SERVICE_URL);
		}

		return doGetAll();
	}
	
	private String doGetAll() throws FogbowException {
		String fullUrl = fogbowCliHttpUtil.getUrl() + ENDPOINT;

		HashMap headers = new HashMap<>();
		HashMap body = new HashMap<>();

		HttpResponse httpResponse = fogbowCliHttpUtil.doGenericRequest(HttpMethod.GET, fullUrl, headers, body);
		return  httpResponse.getContent();
	}

	public void setFogbowCliHttpUtil(FogbowCliHttpUtil fogbowCliHttpUtil) {
		this.fogbowCliHttpUtil = fogbowCliHttpUtil;
	}
}
