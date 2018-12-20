package org.fogbowcloud.cli.order;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.fogbowcloud.cli.HttpRequestMatcher;
import org.fogbowcloud.cli.HttpUtil;
import org.fogbowcloud.cli.exceptions.FogbowCLIException;
import org.fogbowcloud.cli.order.volume.Volume;
import org.fogbowcloud.cli.order.volume.VolumeCommand;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
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
                        OrderCommand.FEDERATION_TOKEN_PATH_COMMAND_KEY, tokenFilePath,
                        OrderCommand.URL_COMMAND_KEY, this.url
                );

        assertEquals(expectedToken, this.orderCommand.getFederationToken());
    }
}