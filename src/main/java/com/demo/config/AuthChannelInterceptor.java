package com.demo.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;



    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authHeaders = accessor.getNativeHeader("Authorization");
            if (authHeaders != null && !authHeaders.isEmpty()) {
                String token = authHeaders.getFirst();
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }

                try {
                    // Extract user ID from token
                    String userId = jwtUtil.getUserIdFromToken(token);
                    if (userId != null) {
                        log.info("Authenticated WebSocket connection for user: {}", userId);

                        // Create authentication object
                        Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, List.of());
                        accessor.setUser(auth);

                        // Also store in session attributes
                        accessor.setSessionAttributes(Map.of("userId", userId));
                    }
                } catch (Exception e) {
                    log.error("Error authenticating WebSocket connection: {}", e.getMessage());
                }
            }
        }
        return message;
    }
}