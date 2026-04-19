package com.ecom.notification;

import com.ecom.notification.payload.OrderCreatedEvent;
import com.ecom.notification.payload.OrderStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderEventConsumer {
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void handleOrderEvent(OrderCreatedEvent orderEvent) {
        System.out.println("Received order event: " + orderEvent);

        long orderId = orderEvent.getOrderId();
        OrderStatus orderStatus = orderEvent.getStatus();

        System.out.println("Order ID: " + orderId);
        System.out.println("Order Status: " + orderStatus);

        // Use case:
        // Sending notifications
        // Sending emails
        // Sending invoices
        // Sending seller notifications
    }
}
