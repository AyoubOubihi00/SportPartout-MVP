package fr.monclub.auth_service.config.JWT;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String CLAIMS_HEADER = "X-JWT-Claims";
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String claimsBase64 = request.getHeader(CLAIMS_HEADER);

        if (claimsBase64 != null && !claimsBase64.isEmpty()) {
            try {
                String claimsJson = new String(Base64.getDecoder().decode(claimsBase64));

                Map claims = objectMapper.readValue(claimsJson, Map.class);

                String username = (String) claims.get("sub");
                String role = (String) claims.get("role");

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authentication = getUsernamePasswordAuthenticationToken(role, username);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                System.err.println("Erreur de traitement des claims Envoy : " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private static UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String role, String username) {
        String roleWithPrefix = (role != null && !role.toUpperCase().startsWith("ROLE_")) ? "ROLE_" + role.toUpperCase() : role.toUpperCase();

        Collection<SimpleGrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(roleWithPrefix));

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}
