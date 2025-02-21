package com.example.demo.elasticsearch.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.demo.elasticsearch.bean.Goods;

public interface GoodsRepository extends ElasticsearchRepository<Goods, String> {
    
    List<Goods> findByPriceBetween(Double minPrice, Double maxPrice);
    
    @Query("{\"match\": {\"name\": \"?0\"}}")
    Page<Goods> searchByName(String name, Pageable pageable);
}
