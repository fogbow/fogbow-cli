package cloud.fogbow.cli;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.connectivity.HttpResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;


public class HttpClientMocker {

    public static final String DEFAULT_HTTP_BODY_RETURN = "Return Irrelevant";
    public static final int DEFAULT_HTTP_STATUS_CODE = 200;
    public static final HashMap DEFAULT_HTTP_HEADERS = new HashMap<>();

    public static FogbowCliHttpUtil init() throws FogbowException {
        return init(DEFAULT_HTTP_BODY_RETURN, DEFAULT_HTTP_STATUS_CODE, DEFAULT_HTTP_HEADERS);
    }

    public static FogbowCliHttpUtil init(String httpReturnContent, int httpStatusCode, Map<String, List<String>> headers) throws FogbowException {
        FogbowCliHttpUtil mockedCliFogbowHttpUtil = spy(new FogbowCliHttpUtil());

        HttpResponse httpResponse = new HttpResponse(httpReturnContent, httpStatusCode, headers);

        // Mock genericRequest
        doReturn(httpResponse).when(mockedCliFogbowHttpUtil).doGenericRequest(
                any(HttpMethod.class), anyString(), any(HashMap.class), any(HashMap.class));

        // Mock all methods for genericAuthenticatedRequest
        doReturn(httpReturnContent).when(mockedCliFogbowHttpUtil).doGenericAuthenticatedRequest(
                any(HttpMethod.class), anyString(), any(HashMap.class), any(HashMap.class));

        doReturn(httpReturnContent).when(mockedCliFogbowHttpUtil).doGenericAuthenticatedRequest(
                any(HttpMethod.class), anyString(), any(HashMap.class));

        doReturn(httpReturnContent).when(mockedCliFogbowHttpUtil).doGenericAuthenticatedRequest(
                any(HttpMethod.class), anyString());


        return mockedCliFogbowHttpUtil;
    }
}
