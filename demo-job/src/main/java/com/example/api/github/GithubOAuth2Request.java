package com.example.api.github;

import com.example.api.HttpRequestBean;
import lombok.Data;

@Data
public class GithubOAuth2Request extends HttpRequestBean {
    private String clientId;

}
