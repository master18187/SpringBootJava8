package com.example.demo.elasticsearch.bean;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Routing;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "goods", createIndex = true)
@Setting(
    settingPath = "elasticsearch/settings.json",
    shards = 3, 
    replicas = 2
)
public class Goods {
    @Id
    private String id;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;
    
    @Field(type = FieldType.Double)
    private Double price;
    
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createTime;

    // @Routing(value = "")
    private String category;
}
