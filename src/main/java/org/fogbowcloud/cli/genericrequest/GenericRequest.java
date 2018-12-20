package org.fogbowcloud.cli.genericrequest;

import com.beust.jcommander.Parameter;
import org.fogbowcloud.cli.utils.KeyValueUtil;

import java.util.Map;

public class GenericRequest {

    private static final String METHOD_COMMAND_KEY = "--method";
    private static final String GENERIC_REQUEST_URL_COMMAND_KEY = "--genericRequestUrl";
    private static final String HEADERS_COMMAND_KEY = "--headers";
    private static final String BODY_COMMAND_KEY = "--body";

    @Parameter(names = METHOD_COMMAND_KEY)
    private String method;

    @Parameter(names = GENERIC_REQUEST_URL_COMMAND_KEY)
    private String url;

    @Parameter(names = HEADERS_COMMAND_KEY, converter = KeyValueUtil.KeyValueConverter.class)
    private Map<String, String> headers;

    @Parameter(names = BODY_COMMAND_KEY, converter = KeyValueUtil.KeyValueConverter.class)
    private Map<String, String> body;

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}