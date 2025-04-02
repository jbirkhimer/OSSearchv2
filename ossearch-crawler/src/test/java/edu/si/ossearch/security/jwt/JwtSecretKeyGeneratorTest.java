package edu.si.ossearch.security.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Base64;

public class JwtSecretKeyGeneratorTest {

    @Test
    public void generateSecretKey() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String secretString = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Generated JWT Secret Key (save this securely and use in your application.properties):");
        System.out.println(secretString);
    }
}