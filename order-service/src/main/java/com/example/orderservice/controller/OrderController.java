package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.util.ModelMapperUtils;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/order-service")
@RestController
public class OrderController {
    private final Environment env;
    private final OrderService orderService;
    private final ModelMapperUtils modelMapperUtils;

    @GetMapping("/health_check")
    public String status() {
        return String.format("[UserController.status] It's working!\n" +
                "port: %s", env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createUser(
            @PathVariable("userId") String userId,
            @RequestBody RequestOrder orderDetails) {
        OrderDto orderDto =  modelMapperUtils.mapper().map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);
        OrderDto createdOrder = orderService.createOrder(orderDto);

        ResponseOrder responseOrder = modelMapperUtils.mapper().map(createdOrder, ResponseOrder.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrders(
            @PathVariable("userId") String userId) {
        List<OrderEntity> orders = (List<OrderEntity>) orderService.getOrdersByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(
                orders.stream()
                .map(orderEntity -> modelMapperUtils.mapper().map(orderEntity, ResponseOrder.class))
                .collect(Collectors.toList())
        );
    }
}
