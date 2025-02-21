package com.example.demo.test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.elasticsearch.bean.Product;
import com.example.demo.elasticsearch.service.ProductService;
import com.example.demo.kafka.service.KafkaService;

@RestController
public class TestController {

    @Autowired
    private KafkaService kafkaService;
    
    @Autowired
    private ProductService productService;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @PostMapping("/send")
    public String sendToKafka(@RequestParam String message) {
        kafkaService.sendMessage("my-topic", message);
        return "Message sent";
    }

    @PostMapping("/product")
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @GetMapping("/products")
    public List<Product> searchProducts(@RequestParam String name) {
        return productService.searchProducts(name);
    }
}
