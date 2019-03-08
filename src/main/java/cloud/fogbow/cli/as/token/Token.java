package cloud.fogbow.cli.as.token;

import cloud.fogbow.common.constants.FogbowConstants;
import cloud.fogbow.common.util.GsonHolder;
import com.google.gson.annotations.SerializedName;

public class Token {
    @SerializedName(FogbowConstants.TOKEN_VALUE_KEY)
    private String tokenValue;

    public static Token fromJson(String json) {
        return GsonHolder.getInstance().fromJson(json, Token.class);
    }

    public String getTokenValue() {
        return tokenValue;
    }
}
