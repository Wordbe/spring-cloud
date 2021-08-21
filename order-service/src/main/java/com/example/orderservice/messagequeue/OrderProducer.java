package com.example.orderservice.messagequeue;

import com.example.orderservice.dto.*;
import com.example.orderservice.util.ModelMapperUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrderProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ModelMapperUtils modelMapperUtils;

    public OrderDto send(String topic, OrderDto orderDto) {
        List<Field> fields = Arrays.asList(
                new Field("string", true, "order_id"),
                new Field("string", true, "user_id"),
                new Field("string", true, "product_id"),
                new Field("int32", true, "quantity"),
                new Field("int32", true, "unit_price"),
                new Field("int32", true, "total_price")
        );

        Schema schema = Schema.builder()
                .type("struct")
                .fields(fields)
                .optional(false)
                .name("orders")
                .build();

        Payload payload = modelMapperUtils.mapper().map(orderDto, Payload.class);

        KafkaOrderDto kafkaOrderDto = KafkaOrderDto.builder()
                .schema(schema)
                .payload(payload)
                .build();


        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";

        try {
            jsonInString = mapper.writeValueAsString(kafkaOrderDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("OrderProducer(카프카) 가 order-microservice 로부터 데이터를 전송: " + kafkaOrderDto);

        return orderDto;
    }
}
