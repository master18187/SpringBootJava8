package com.example.api;

import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class OAuth2Client<Req extends HttpRequestBean, Resp extends HttpResponseBean>
        extends BaseClient<Req, Resp> {

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String authUrl;
    private final String tokenUrl;

    public OAuth2Client(String clientId, String clientSecret, String redirectUri,
                        String authUrl, String tokenUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.authUrl = authUrl;
        this.tokenUrl = tokenUrl;
    }

    public String createAuthUrl(String state) {
        return String.format("%s?response_type=code&client_id=%s&redirect_uri=%s&state=%s",
                authUrl, clientId, redirectUri, state);
    }

    public Object fetchAccessToken(String code) {
        // 实现获取access token的逻辑
        return null;
    }

    @Override
    protected void addAuthHeaders(Request.Builder builder, Req req) {

    }

    @Override
    protected Resp handleResponse(Response response, Req req) throws IOException {
        return null;
    }

    // 其他OAuth2特定方法...
}
