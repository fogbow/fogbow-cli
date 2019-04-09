package cloud.fogbow.cli;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.connectivity.HttpResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;


public class HttpClientMocker {

    public static FogbowCliHttpUtil init() throws FogbowException {
        return init("some text", 200, new HashMap<>());
    }

    public static FogbowCliHttpUtil init(String httpReturnContent, int httpStatusCode, Map<String, List<String>> headers) throws FogbowException {
        FogbowCliHttpUtil mockedCliFogbowHttpUtil = spy(new FogbowCliHttpUtil());

        HttpResponse httpResponse = new HttpResponse(httpReturnContent, httpStatusCode, headers);

        doReturn(httpResponse).when(mockedCliFogbowHttpUtil).doGenericRequest(
                any(HttpMethod.class), anyString(), any(HashMap.class), any(HashMap.class));


        return mockedCliFogbowHttpUtil;
    }
}
