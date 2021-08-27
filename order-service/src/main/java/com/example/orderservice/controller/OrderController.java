package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.messagequeue.OrderProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.util.ModelMapperUtils;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/order-service")
@RestController
public class OrderController {
    private final Environment env;
    private final OrderService orderService;
    private final ModelMapperUtils modelMapperUtils;
    private final KafkaProducer kafkaProducer;
    private final OrderProducer orderProducer;

    @GetMapping("/health_check")
    public String status() {
        return String.format("[UserController.status] It's working!\n" +
                "port: %s", env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(
            @PathVariable("userId") String userId,
            @RequestBody RequestOrder orderDetails) {
        OrderDto orderDto =  modelMapperUtils.mapper().map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);

        log.info("[order service] order data 추가하기 전...");

        // JPA 로 주문 생성
        OrderDto createdOrder = orderService.createOrder(orderDto);
        ResponseOrder responseOrder = modelMapperUtils.mapper().map(createdOrder, ResponseOrder.class);

        // Kafka Topic 으로 주문 생성
//        orderDto.setOrderId(UUID.randomUUID().toString());
//        orderDto.setTotalPrice(orderDetails.getQuantity() * orderDetails.getUnitPrice());
//        ResponseOrder responseOrder = modelMapperUtils.mapper().map(orderDto, ResponseOrder.class);
//        orderProducer.send("orders", orderDto);

//        kafkaProducer.send("catalog-topic", orderDto);

        log.info("[order service] order data 추가되었음");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrders(
            @PathVariable("userId") String userId) {
        log.info("[order service] order data 받기 전...");
        List<OrderEntity> orders = (List<OrderEntity>) orderService.getOrdersByUserId(userId);

        try {
            Thread.sleep(1000);
            throw new RuntimeException("장애 발생");
        } catch (InterruptedException e) {
            log.warn(e.getMessage());
        }

        log.info("[order service] order data 받았음");
        return ResponseEntity.status(HttpStatus.OK).body(
                orders.stream()
                .map(orderEntity -> modelMapperUtils.mapper().map(orderEntity, ResponseOrder.class))
                .collect(Collectors.toList())
        );
    }
}
