package cloud.fogbow.cli.ms.member;

import java.io.IOException;

import cloud.fogbow.cli.HttpCliConstants;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import org.apache.http.HttpResponse;
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

	public String run() throws IOException {
		if (this.url == null || this.url.isEmpty()) {
			throw new ParameterException(Messages.Exception.NO_MEMBERSHIP_SERVICE_URL);
		}

		if (this.isGetAllCommand) {
			return doGetAll();
		}

		throw new ParameterException(Messages.Exception.INCOMPLETE_COMMAND);
	}
	
	private String doGetAll() throws ParseException, IOException {
		String fullUrl = this.url + ENDPOINT;
		HttpResponse httpResponse = HttpCliConstants.get(fullUrl);
		return HttpCliConstants.getHttpEntityAsString(httpResponse);
	}
}
