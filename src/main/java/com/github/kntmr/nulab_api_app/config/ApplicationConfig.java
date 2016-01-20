package com.github.kntmr.nulab_api_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfig {

    @Value("${backlog.spaceId}")
    private String spaceId;

    @Value("${backlog.apiKey}")
    private String apiKey;

    @Value("${http.proxyHost:}")
    private String proxyHost;

    @Value("${http.proxyPort:8080}")
    private int proxyPort;

    public String getSpaceId() {
        return spaceId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }
}
