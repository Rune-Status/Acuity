package com.acuitybotting.db.arango.acuity.identities.service;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityBottingUser;
import com.acuitybotting.db.arango.acuity.identities.domain.Principal;
import com.acuitybotting.db.arango.acuity.identities.repositories.AcuityBottingUserRepository;
import com.acuitybotting.security.acuity.encryption.AcuityEncryptionService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zachary Herridge on 8/9/2018.
 */
@Service
@Slf4j
public class AcuityUsersService {

    @Value("{jwt.secret}")
    private String jwtSecret;

    private final AcuityBottingUserRepository userRepository;
    private final AcuityEncryptionService encryptionService;

    @Autowired
    public AcuityUsersService(AcuityBottingUserRepository userRepository, AcuityEncryptionService encryptionService) {
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
    }

    public Optional<AcuityBottingUser> findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Optional<AcuityBottingUser> login(String email, String password) {
        return userRepository.findByEmail(email).filter(acuityBottingUser -> encryptionService.comparePassword(acuityBottingUser.getPasswordHash(), password));
    }

    public String createLinkJwt(Principal principal){
        return JWT.create()
                .withIssuer("acuitybotting")
                .withClaim("source", principal.getType())
                .withClaim("sourceType", principal.getUid())
                .withExpiresAt(Date.from(Instant.now().plus(Duration.of(10, ChronoUnit.MINUTES))))
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    public boolean linkToPrincipal(String uid, String jwt){
        DecodedJWT verify;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtSecret))
                    .acceptLeeway(TimeUnit.HOURS.toMillis(36))
                    .build();
            verify = verifier.verify(jwt);
        }
        catch (Throwable e){
            log.warn("Could not verify jwt.");
            return false;
        }

        String sourceUid = verify.getClaims().get("source").asString();
        String sourceType = verify.getClaims().get("sourceType").asString();

        if (sourceType == null || sourceUid == null) return false;

        Principal principal = Principal.of(sourceType, sourceUid);

        AcuityBottingUser user = userRepository.findByPrincipalId(uid).orElse(null);
        if (user == null) return false;

        try {
            if (user.getLinkedPrincipals() == null) user.setLinkedPrincipals(new HashSet<>());
            user.getLinkedPrincipals().add(principal);
            userRepository.save(user);
            return true;
        }
        catch (Throwable e){
            log.error("Error during linking principals.", e);
        }

        return false;
    }

    public boolean register(String email, String displayName, String password) {
        Objects.requireNonNull(email);
        Objects.requireNonNull(displayName);
        Objects.requireNonNull(password);

        AcuityBottingUser user = new AcuityBottingUser();
        user.setPrincipalId(UUID.randomUUID().toString());
        user.setEmail(email.toLowerCase());
        user.setPasswordHash(encryptionService.encodePassword(password));
        user.setDisplayName(displayName);
        user.setLinkedPrincipals(new HashSet<>());

        try {
            userRepository.save(user);
            return true;
        } catch (Throwable e) {
            log.error("Error during registration.", e);
        }

        return false;
    }
}
