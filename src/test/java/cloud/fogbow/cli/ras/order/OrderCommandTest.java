package cloud.fogbow.cli.ras.order;

import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.ras.order.volume.Volume;
import cloud.fogbow.cli.ras.order.volume.VolumeCommand;
import com.beust.jcommander.JCommander;
import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class OrderCommandTest {

    private Volume volume;

    private OrderCommand orderCommand;

    private final String url = "my-url";
    private final String token = "my-token";
    private final String id = "my-id";
    private final Map<String, String> requirements = new HashMap<>();

    private final String APP_REPOSITORY_DIRECTORY = System.getProperty("user.dir");

    @Before
    public void setUp() throws ClientProtocolException, IOException {
        this.volume = new Volume(
                "my-provider",
                1024,
                "volume-name",
                requirements
        );
        this.orderCommand = new OrderCommand(VolumeCommand.ENDPOINT, this.volume);
    }

    @Test
    public void testGetFederationTokenPassingTokenFilePath() throws FogbowCLIException {

        String resourcesPath = "src/test/resource/";
        String fileName = "get_content";
        String tokenFilePath = APP_REPOSITORY_DIRECTORY + "/" + resourcesPath + fileName;

        String expectedToken = "test-get-content";

        JCommander.newBuilder()
                .addObject(this.orderCommand)
                .build()
                .parse(
                        OrderCommand.CREATE_COMMAND_KEY,
                        CliCommonParameters.FEDERATION_TOKEN_PATH_COMMAND_KEY, tokenFilePath,
                        CliCommonParameters.URL_COMMAND_KEY, this.url
                );

        assertEquals(expectedToken, this.orderCommand.getFederationToken());
    }
}