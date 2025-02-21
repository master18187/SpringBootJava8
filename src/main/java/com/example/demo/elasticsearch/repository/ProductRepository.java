package com.example.demo.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.demo.elasticsearch.bean.Product;

public interface ProductRepository extends ElasticsearchRepository<Product, String> {
    List<Product> findByName(String name);
}
