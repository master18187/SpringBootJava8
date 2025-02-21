package com.example.demo.elasticsearch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import com.example.demo.elasticsearch.bean.Goods;

/** 索引管理 */
@Service
public class IndexService {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public void createProductIndex() {
        elasticsearchOperations.indexOps(Goods.class).create();
    }

    public boolean indexExists() {
        return elasticsearchOperations.indexOps(Goods.class).exists();
    }
}
