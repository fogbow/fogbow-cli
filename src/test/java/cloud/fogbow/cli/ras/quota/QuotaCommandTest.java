package cloud.fogbow.cli.ras.quota;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.HttpClientMocker;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.ras.order.OrderCommand;
import cloud.fogbow.cli.ras.order.compute.Compute;
import cloud.fogbow.cli.ras.order.compute.ComputeCommand;
import cloud.fogbow.cli.utils.CommandUtil;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import com.beust.jcommander.JCommander;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.mockito.Mockito.verify;

public class QuotaCommandTest {

    private QuotaCommand quotaCommand;
    private FogbowCliHttpUtil fogbowCliHttpUtil;

    private String url = "url";
    private String token = "token";
    private String cloudName = "cloud-name";
    private String memberId = "member-id";

    @Before
    public void setUp() throws FogbowException {
        this.quotaCommand = new QuotaCommand();

        this.fogbowCliHttpUtil = HttpClientMocker.init();
        quotaCommand.setFogbowCliHttpUtil(fogbowCliHttpUtil);
    }

    // test case: sasaasasas
    @Test
    public void testRun() throws FogbowException {
        JCommander.newBuilder()
                .addObject(this.quotaCommand)
                .build()
                .parse(
                        CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, this.token,
                        CliCommonParameters.URL_COMMAND_KEY, this.url,
                        CliCommonParameters.MEMBER_ID_COMMAND_KEY, this.memberId,
                        CliCommonParameters.CLOUD_NAME_COMMAND_KEY, this.cloudName
                );

        String path = QuotaCommand.ENDPOINT + '/' + this.memberId + '/' + this.cloudName;
        this.quotaCommand.run();

        verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.GET, path);
    }
}
