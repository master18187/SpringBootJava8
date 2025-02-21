package com.example.demo.elasticsearch.controller;

import java.io.IOException;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 监控与健康检查
 */
@RestController
public class ClusterHealthController {

    @Autowired
    private RestHighLevelClient client;

    @GetMapping("/cluster-health")
    public ClusterHealthStatus getClusterHealth() throws IOException {
        ClusterHealthResponse response = client.cluster().health(
            new ClusterHealthRequest(), RequestOptions.DEFAULT
        );
        return response.getStatus();
    }
}
