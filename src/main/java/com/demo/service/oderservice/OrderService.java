package com.demo.service.oderservice;

import com.demo.model.Order;
import com.demo.model.Product;
import com.demo.model.dto.CreateOrderRequest;
import com.demo.model.dto.OrderResponseDto;
import com.demo.repo.OrderRepository;
import com.demo.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final FakePaymentGatewayService paymentService;

    public OrderResponseDto createOrder(CreateOrderRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.isActive()) {
            throw new RuntimeException("Product is currently unavailable");
        }

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setProductId(product.getId());
        order.setAmount(product.getPrice());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);

        // Trigger the async dummy payment
//        paymentService.initiatePayment(saved);

        return mapToDto(saved);
    }

    public OrderResponseDto getOrderStatus(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapToDto(order);
    }

    private OrderResponseDto mapToDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setAmount(order.getAmount());
        return dto;
    }
}