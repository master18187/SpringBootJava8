package com.example.demo.elasticsearch.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.elasticsearch.bean.Goods;
import com.example.demo.elasticsearch.repository.GoodsRepository;

@Service
public class GoodsService {

    @Autowired
    private GoodsRepository productRepository;

    public Goods saveProduct(Goods goods) {
        return productRepository.save(goods);
    }

    public List<Goods> bulkSave(List<Goods> goods) {
        Iterable<Goods> savedProducts = productRepository.saveAll(goods);
        List<Goods> result = new ArrayList<>();
        savedProducts.forEach(result::add);
        return result;
    }

    public Page<Goods> searchProducts(String query, Pageable pageable) {
        return productRepository.searchByName(query, pageable);
    }
}
