package com.acuitybotting.db.arango.acuity.identities.service;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityBottingUser;
import com.acuitybotting.db.arango.acuity.identities.repositories.AcuityBottingUserRepository;
import com.acuitybotting.security.acuity.encryption.AcuityEncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Zachary Herridge on 8/9/2018.
 */
@Service
@Slf4j
public class AcuityUsersService {

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

    public boolean register(String email, String displayName, String password) {
        AcuityBottingUser user = new AcuityBottingUser();
        user.setEmail(email);
        user.setPasswordHash(encryptionService.encodePassword(password));
        user.setDisplayName(displayName);

        try {
            userRepository.save(user);
            return true;
        } catch (Throwable e) {
            log.error("Error during registration.", e);
        }

        return false;
    }
}
