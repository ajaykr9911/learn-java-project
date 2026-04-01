package com.demo.service.oderservice;

import com.demo.model.Cart;
import com.demo.model.Cart.CartItem;
import com.demo.model.Product;
import com.demo.model.dto.CartRequest;
import com.demo.model.dto.CartResponse;
import com.demo.repo.CartRepository;
import com.demo.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    // ── GET CART ──────────────────────────────────────────────
    public CartResponse getCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        return toDto(cart);
    }

    // ── ADD TO CART ───────────────────────────────────────────
    public CartResponse addToCart(String userId, CartRequest req) {
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < req.getQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }
        if (req.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be at least 1");
        }

        Cart cart = getOrCreateCart(userId);
        List<CartItem> items = cart.getItems();

        // If already in cart → increment quantity
        CartItem existing = items.stream()
                .filter(i -> i.getProductId().equals(req.getProductId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            int newQty = existing.getQuantity() + req.getQuantity();
            if (product.getStock() < newQty) {
                throw new RuntimeException("Not enough stock for quantity " + newQty);
            }
            existing.setQuantity(newQty);
        } else {
            CartItem item = new CartItem();
            item.setProductId(product.getId());
            item.setName(product.getName());
            item.setImage(product.getImages());
            item.setPrice(product.getPrice());
            item.setDiscountPrice(product.getDiscountPrice());
            item.setQuantity(req.getQuantity());
            items.add(item);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        return toDto(cartRepository.save(cart));
    }

    // ── UPDATE QUANTITY ───────────────────────────────────────
    public CartResponse updateQuantity(String userId, CartRequest req) {
        if (req.getQuantity() <= 0) {
            return removeItem(userId, req.getProductId());
        }

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < req.getQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        Cart cart = getOrCreateCart(userId);
        cart.getItems().stream()
                .filter(i -> i.getProductId().equals(req.getProductId()))
                .findFirst()
                .ifPresent(i -> i.setQuantity(req.getQuantity()));

        cart.setUpdatedAt(LocalDateTime.now());
        return toDto(cartRepository.save(cart));
    }

    // ── REMOVE ITEM ───────────────────────────────────────────
    public CartResponse removeItem(String userId, String productId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        cart.setUpdatedAt(LocalDateTime.now());
        return toDto(cartRepository.save(cart));
    }

    // ── CLEAR CART (called after order placed) ────────────────
    public void clearCart(String userId) {
        cartRepository.findByUserId(userId)
                .ifPresent(cart -> {
                    cart.getItems().clear();
                    cart.setUpdatedAt(LocalDateTime.now());
                    cartRepository.save(cart);
                });
    }

    // ── HELPERS ───────────────────────────────────────────────
    private Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUserId(userId);
                    c.setUpdatedAt(LocalDateTime.now());
                    return cartRepository.save(c);
                });
    }

    private CartResponse toDto(Cart cart) {
        CartResponse res = new CartResponse();
        res.setCartId(cart.getId());
        res.setUserId(cart.getUserId());

        List<CartResponse.CartItemDto> itemDtos = cart.getItems().stream().map(i -> {
            CartResponse.CartItemDto d = new CartResponse.CartItemDto();
            d.setProductId(i.getProductId());
            d.setName(i.getName());
            d.setImage(i.getImage());
            d.setPrice(i.getPrice());
            d.setDiscountPrice(i.getDiscountPrice());
            d.setQuantity(i.getQuantity());
            d.setLineTotal(i.lineTotal());
            return d;
        }).toList();

        res.setItems(itemDtos);
        res.setCartTotal(itemDtos.stream().mapToDouble(CartResponse.CartItemDto::getLineTotal).sum());
        return res;
    }
}