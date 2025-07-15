package com.example.api.github;

import com.example.api.OAuth2Client;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class GithubOAuth2Client extends OAuth2Client<GithubOAuth2Request, GithubOAuth2Response> {

    public GithubOAuth2Client(String clientId, String clientSecret, String redirectUri,
                        String authUrl, String tokenUrl) {
        super(clientId, clientSecret, redirectUri, authUrl, tokenUrl);
        this.connectTimeout = 10L; // 10秒连接超时
        this.readTimeout = 30L;    // 30秒读取超时
    }


    // 实现OAuth2特有的方法
    @Override
    protected void addAuthHeaders(Request.Builder builder, GithubOAuth2Request req) {

    }



    @Override
    protected GithubOAuth2Response handleResponse(Response response, GithubOAuth2Request req) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        String responseBody = response.body().string();
        GithubOAuth2Response weatherResp = new GithubOAuth2Response();

        // 简单解析JSON响应

        return weatherResp;
    }

    @Override
    protected Request buildRequest(GithubOAuth2Request req) {
        HttpUrl url = HttpUrl.parse("https://api.openweathermap.org/data/2.5/weather")
                .newBuilder()

                .build();

        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }
}
