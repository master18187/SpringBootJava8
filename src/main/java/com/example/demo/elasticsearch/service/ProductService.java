package com.example.demo.elasticsearch.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.elasticsearch.bean.Product;
import com.example.demo.elasticsearch.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // 创建文档
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // 查询文档
    public List<Product> searchProducts(String name) {
        return productRepository.findByName(name);
    }

    // 删除文档
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
}
