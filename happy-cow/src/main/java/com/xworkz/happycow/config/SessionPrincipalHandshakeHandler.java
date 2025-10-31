package com.xworkz.happycow.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;

public class SessionPrincipalHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest) {
            HttpSession session = ((ServletServerHttpRequest) request)
                    .getServletRequest().getSession(false);

            if (session != null) {
                Object adminObj = session.getAttribute("loggedInAdmin");
                if (adminObj instanceof com.xworkz.happycow.dto.AdminDTO) {
                    String email = ((com.xworkz.happycow.dto.AdminDTO) adminObj).getEmailId();
                    return new UserPrincipal(email); // uniquely identifies user
                }
            }
        }
        // fallback: anonymous id
        return new UserPrincipal("anon-" + UUID.randomUUID());
    }

    static class UserPrincipal implements Principal {
        private final String name;
        UserPrincipal(String name) { this.name = name; }
        @Override
        public String getName() { return name; }
    }
}
