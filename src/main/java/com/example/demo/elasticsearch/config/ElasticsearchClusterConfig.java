package com.example.demo.elasticsearch.config;

import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.repository")
public class ElasticsearchClusterConfig {

    @Bean
    public RestHighLevelClient elasticsearchClient() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
            AuthScope.ANY,
            new UsernamePasswordCredentials("elastic", "your_password")
        );

        return new RestHighLevelClient(
            RestClient.builder(
                new HttpHost("node1", 9200, "http"),
                new HttpHost("node2", 9200, "http"),
                new HttpHost("node3", 9200, "http")
            )
            .setHttpClientConfigCallback(httpClientBuilder -> {
                httpClientBuilder
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .setSSLContext(createSSLContext());
                return httpClientBuilder;
            })
        );
    }

    private SSLContext createSSLContext() {
        try {
            KeyStore truststore = KeyStore.getInstance("PKCS12");
            truststore.load(getClass().getResourceAsStream("/elastic-truststore.p12"), 
                          "changeit".toCharArray());
            
            return SSLContextBuilder.create()
                .loadTrustMaterial(truststore, null)
                .build();
        } catch (Exception e) {
            throw new RuntimeException("SSL context creation failed", e);
        }
    }

    /**
     * 自动重试策略
     * @return
     */
    // @Bean
    // public ElasticsearchCustomizer elasticsearchCustomizer() {
    //     return client -> {
    //         client.setRetryOnTimeout(true);
    //         client.setMaxRetryTimeoutMillis(15000);
    //     };
    // }
}
