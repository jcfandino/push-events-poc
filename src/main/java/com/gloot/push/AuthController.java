package com.gloot.push;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  @GetMapping("/auth/token")
  public String getToken() {
    var algorithm = Algorithm.HMAC256("secr3t");
    var permissions = List.of("read", "write", "configure");
    var scope =
        permissions
            .stream()
            .map(p -> String.format("rabbitmq.%s:*/*/*", p))
            .collect(Collectors.toList());
    var token =
        JWT.create()
            .withKeyId("legacy-token-key")
            .withAudience("rabbitmq")
            .withArrayClaim("scope", scope.toArray(new String[] {}))
            .withExpiresAt(Date.from(Instant.now().plusSeconds(3600)))
            .sign(algorithm);
    return token;
  }
}
