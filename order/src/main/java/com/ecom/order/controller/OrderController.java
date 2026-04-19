package com.ecom.order.controller;


import com.ecom.order.dto.OrderResponse;
import com.ecom.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/createOrder")
    public ResponseEntity<OrderResponse> createOrder(@RequestHeader("X-User-ID") String userId){
        return orderService.createOrder(userId)
                .map(orderResponse -> new ResponseEntity<>(orderResponse,HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("Hello World");
    }


}
