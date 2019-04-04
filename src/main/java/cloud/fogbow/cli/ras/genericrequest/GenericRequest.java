package cloud.fogbow.cli.ras.genericrequest;

import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.ras.FogbowCliResource;
import com.beust.jcommander.Parameter;
import cloud.fogbow.cli.utils.KeyValueUtil;
import com.sun.mail.imap.protocol.BODY;

import java.util.HashMap;
import java.util.Map;

public class GenericRequest implements FogbowCliResource {
    private static final String METHOD_COMMAND_KEY = "--method";
    private static final String GENERIC_REQUEST_URL_COMMAND_KEY = "--genericRequestUrl";
    private static final String HEADERS_COMMAND_KEY = "--headers";
    private static final String BODY_COMMAND_KEY = "--body";

    private static final String BODY_JSON_KEY = "body";
    private static final String HEADERS_JSON_KEY = "headers";
    private static final String METHOD_JSON_KEY = "method";
    private static final String URL_JSON_KEY = "url";

    @Parameter(names = METHOD_COMMAND_KEY, description = Documentation.GenericRequest.METHOD, required = true)
    private String method;

    @Parameter(names = GENERIC_REQUEST_URL_COMMAND_KEY, description = Documentation.GenericRequest.URL, required = true)
    private String url;

    @Parameter(names = HEADERS_COMMAND_KEY, description = Documentation.GenericRequest.HEADERS,
            converter = KeyValueUtil.KeyValueConverter.class)
    private HashMap headers;

    @Parameter(names = BODY_COMMAND_KEY, description = Documentation.GenericRequest.BODY,
            converter = KeyValueUtil.KeyValueConverter.class)
    private HashMap body;

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public HashMap getHeaders() {
        return headers;
    }

    public HashMap getBody() {
        return body;
    }

    @Override
    public HashMap getHTTPHashMap() {
        HashMap body = new HashMap();

        if(this.body == null){
            this.body = new HashMap();
        }
        body.put(BODY_JSON_KEY, this.body);

        if(this.headers == null){
            this.headers = new HashMap();
        }
        body.put(HEADERS_JSON_KEY, this.headers);

        body.put(METHOD_JSON_KEY, this.method);
        body.put(URL_JSON_KEY, this.url);

        return body;
    }
}