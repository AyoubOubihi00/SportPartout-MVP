package fr.monclub.bff.config.JWT;

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
import java.util.*;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String CLAIMS_HEADER = "X-JWT-Claims";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("jwt filter hehe");
        String claimsBase64 = request.getHeader(CLAIMS_HEADER);
        System.out.println("Claims Base64: " + claimsBase64);

        if (claimsBase64 != null && !claimsBase64.isEmpty()) {
            try {
                String claimsJson = new String(Base64.getDecoder().decode(claimsBase64));

                Map<String, Object> claims = objectMapper.readValue(claimsJson, Map.class); // Utilisation d'une Map typée

                String username = (String) claims.get("sub");
                String role = (String) claims.get("role");
                // **🔑 Récupération de l'ID ici**
                Object userIdClaim = claims.get("userId");
                // Assurez-vous que userId est un Long (ou String, selon la façon dont il est encodé)
                UUID userId = UUID.fromString(userIdClaim.toString());

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // **Création d'un Principal personnalisé contenant l'ID**
                    UserSimple principal = new UserSimple(userId,username);

                    UsernamePasswordAuthenticationToken authentication = getUsernamePasswordAuthenticationToken(role, principal);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                System.err.println("Erreur de traitement des claims Envoy : " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    // Modification pour accepter l'objet Principal
    private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String role, UserSimple principal) {
        String roleWithPrefix = (role != null && !role.toUpperCase().startsWith("ROLE_")) ? "ROLE_" + role.toUpperCase() : role.toUpperCase();

        Collection<SimpleGrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(roleWithPrefix));

        // Note: Le premier argument est le Principal
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }
}