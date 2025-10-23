package fr.monclub.auth_service.controller;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

@RestController
public class JwksController {

    @Value("classpath:keys/public.pem")
    private Resource publicKeyResource;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwks() {
        try {
            RSAPublicKey publicKey = (RSAPublicKey) getPublicKey();

            RSAKey jwk = new RSAKey.Builder(publicKey)
                    .keyID("sportpartout-key-1")
                    .algorithm(JWSAlgorithm.RS256)
                    .keyUse(KeyUse.SIGNATURE)
                    .build();

            JWKSet jwkSet = new JWKSet(jwk);
            return jwkSet.toJSONObject();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du JWKS", e);
        }
    }

    private RSAPublicKey getPublicKey() {
        try (InputStream is = publicKeyResource.getInputStream()) {
            String key = new String(is.readAllBytes(), StandardCharsets.UTF_8)
                    .replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger la clé publique", e);
        }
    }
}