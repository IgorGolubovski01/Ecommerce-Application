package com.ecom.order.service;


import com.ecom.order.clients.product.ProductServiceClient;
import com.ecom.order.clients.user.UserServiceClient;
import com.ecom.order.dto.CartItemRequest;
import com.ecom.order.dto.ProductResponse;
import com.ecom.order.dto.UserResponse;
import com.ecom.order.model.CartItem;
import com.ecom.order.repository.CartItemRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;
    int attempt = 0;

//    @CircuitBreaker(name = "product", fallbackMethod = "addToCartFallBack")
    @Retry(name = "retryBreaker", fallbackMethod = "addToCartFallBack")
    public boolean addToCart(String userId, CartItemRequest request) {

        System.out.println("Attempt count: " + ++attempt);

        ProductResponse productResponse = productServiceClient.getProductDetails(request.getProductId());
        if(productResponse == null || productResponse.getStockQuantity() < request.getQuantity())
            return false;

        UserResponse userResponse = userServiceClient.getUserDetails(userId);
        if(userResponse == null)
            return false;

        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(userId, Long.valueOf(request.getProductId()));
        if(existingCartItem != null){
            existingCartItem.setQuantity(request.getQuantity()+existingCartItem.getQuantity());
            existingCartItem.setPrice(BigDecimal.valueOf(100)); //TODO
            cartItemRepository.save(existingCartItem);
        } else{
            existingCartItem = new CartItem();
            existingCartItem.setUserId(userId);
            existingCartItem.setProductId(Long.valueOf(request.getProductId()));
            existingCartItem.setQuantity(request.getQuantity());
            existingCartItem.setPrice(BigDecimal.valueOf(100));
            cartItemRepository.save(existingCartItem);
        }
        return true;
    }

    public boolean addToCartFallBack(String userId, CartItemRequest request, Exception exception) {
        exception.printStackTrace();
        return false;
    }

//    public boolean deleteItemFromCart(String userId, Long productId) {
//        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
//        if(cartItem != null){
//            cartItemRepository.delete(cartItem);
//            return true;
//        }
//        return false;
//    }
//
    public List<CartItem> getCart(String userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}
