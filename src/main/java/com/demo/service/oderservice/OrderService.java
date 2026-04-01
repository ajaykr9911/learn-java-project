package com.demo.service.oderservice;

import com.demo.model.Cart;
import com.demo.model.Order;
import com.demo.model.Order.OrderItem;
import com.demo.model.Product;
import com.demo.model.dto.OrderResponse;
import com.demo.repo.CartRepository;
import com.demo.repo.OrderRepository;
import com.demo.repo.ProductRepository;
import com.demo.service.oderservice.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    // ── CREATE ORDER FROM CART ────────────────────────────────
    public OrderResponse createOrderFromCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot place order with empty cart");
        }

        // Validate stock for ALL items before touching anything
        for (Cart.CartItem ci : cart.getItems()) {
            Product p = productRepository.findById(ci.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + ci.getName()));
            if (p.getStock() < ci.getQuantity()) {
                throw new RuntimeException("Insufficient stock for: " + p.getName());
            }
        }

        // Snapshot cart → order items & deduct stock
        List<OrderItem> orderItems = cart.getItems().stream().map(ci -> {
            Product p = productRepository.findById(ci.getProductId()).orElseThrow();
            p.setStock(p.getStock() - ci.getQuantity());
            productRepository.save(p);

            OrderItem oi = new OrderItem();
            oi.setProductId(ci.getProductId());
            oi.setName(ci.getName());
            oi.setImage(ci.getImage());
            oi.setPrice(ci.getPrice());
            oi.setDiscountPrice(ci.getDiscountPrice());
            oi.setQuantity(ci.getQuantity());
            oi.setTotalPrice(ci.lineTotal());
            return oi;
        }).toList();

        double total = orderItems.stream().mapToDouble(OrderItem::getTotalPrice).sum();

        Order order = new Order();
        order.setUserId(userId);
        order.setItems(orderItems);
        order.setTotalAmount(total);
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);

        // Clear cart after successful order creation
        cartService.clearCart(userId);

        return toDto(saved);
    }

    // ── GET MY ORDERS ─────────────────────────────────────────
    public List<OrderResponse> getMyOrders(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toDto).toList();
    }

    // ── GET ORDER BY ID ───────────────────────────────────────
    public OrderResponse getOrderById(String orderId, String userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return toDto(order);
    }

    // ── CONFIRM COD ───────────────────────────────────────────
    public OrderResponse confirmCOD(String orderId, String userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!"CREATED".equals(order.getStatus())) {
            throw new RuntimeException("Order already processed");
        }

        order.setStatus("CONFIRMED");
        order.setPaymentMethod("COD");
        order.setUpdatedAt(LocalDateTime.now());
        return toDto(orderRepository.save(order));
    }

    // ── MARK PAID (called from payment webhook) ───────────────
    public void markAsPaid(String orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus("PAID");
            order.setPaymentMethod("STRIPE");
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        });
    }

    // ── MAPPER ───────────────────────────────────────────────
    private OrderResponse toDto(Order order) {
        OrderResponse res = new OrderResponse();
        res.setOrderId(order.getId());
        res.setUserId(order.getUserId());
        res.setStatus(order.getStatus());
        res.setPaymentMethod(order.getPaymentMethod());
        res.setTotalAmount(order.getTotalAmount());

        if (order.getItems() != null) {
            res.setItems(order.getItems().stream().map(i -> {
                OrderResponse.OrderItemDto d = new OrderResponse.OrderItemDto();
                d.setProductId(i.getProductId());
                d.setName(i.getName());
                d.setImage(i.getImage());
                d.setPrice(i.getPrice());
                d.setDiscountPrice(i.getDiscountPrice());
                d.setQuantity(i.getQuantity());
                d.setTotalPrice(i.getTotalPrice());
                return d;
            }).toList());
        }

        return res;
    }
}