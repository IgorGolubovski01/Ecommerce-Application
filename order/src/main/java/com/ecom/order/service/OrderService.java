package com.ecom.order.service;


import com.ecom.order.clients.user.UserServiceClient;
import com.ecom.order.dto.OrderCreatedEvent;
import com.ecom.order.dto.OrderItemDto;
import com.ecom.order.dto.OrderResponse;
import com.ecom.order.dto.UserResponse;
import com.ecom.order.model.CartItem;
import com.ecom.order.model.Order;
import com.ecom.order.model.OrderItem;
import com.ecom.order.model.OrderStatus;
import com.ecom.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public Optional<OrderResponse> createOrder(String userId) {

        List<CartItem> cartItems = cartService.getCart(userId);
        if(cartItems.isEmpty()){
            return Optional.empty();
        }

        UserResponse userResponse = userServiceClient.getUserDetails(userId);
        if(userResponse == null)
            return Optional.empty(); //todo


        BigDecimal totalPrice = cartItems
                .stream()
                .map(cartItem -> {
                    return cartItem.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);

        List<OrderItem> orderItems = cartItems
                .stream()
                .map(cartItem -> new OrderItem(
                        null,
                        cartItem.getProductId(),
                        cartItem.getQuantity(),
                        cartItem.getPrice(),
                        order
                )).toList();

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(userId);

        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getStatus(),
                mapToOrderItemsDto(savedOrder.getItems()),
                savedOrder.getTotalAmount(),
                savedOrder.getCreatedAt()
        );

        rabbitTemplate.convertAndSend(
                exchangeName,
                routingKey,
                event
        );

        return Optional.of(mapToOrderResponse(savedOrder));
    }

    private List<OrderItemDto> mapToOrderItemsDto(List<OrderItem> items){
        return items
                .stream()
                .map(item -> new OrderItemDto(
                        item.getId(),
                        item.getProductId().toString(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice().multiply(new BigDecimal(item.getQuantity()))
                )).collect(Collectors.toList());
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getItems()
                        .stream()
                        .map(orderItem -> new OrderItemDto(
                                orderItem.getId(),
                                String.valueOf(orderItem.getProductId()),
                                orderItem.getQuantity(),
                                orderItem.getPrice(),
                                orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))

                        )).toList(),
                order.getCreatedAt()
        );
    }
}
