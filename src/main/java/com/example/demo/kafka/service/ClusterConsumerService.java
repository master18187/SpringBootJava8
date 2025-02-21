package com.example.demo.kafka.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class ClusterConsumerService {

    @KafkaListener(
        topics = "${kafka.topic.cluster-topic:test}", 
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenClusterMessages(
        @Payload String message,
        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
        @Header(KafkaHeaders.OFFSET) long offset
    ) {
        System.out.printf("Received Message [%s] from partition %d (offset %d)%n",
            message, partition, offset);
    }
}
