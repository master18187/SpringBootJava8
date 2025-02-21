package com.example.demo.elasticsearch.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import lombok.Data;


@Data
@Document(indexName = "products", createIndex = true)
@Setting(settingPath = "elasticsearch/settings.json")
public class Product {
    @Id
    private String id;
    
    @Field(type = FieldType.Text)
    private String name;
    
    @Field(type = FieldType.Double)
    private Double price;

}
