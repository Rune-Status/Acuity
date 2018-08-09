package com.acuitybotting.security.acuity.encryption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 8/9/2018.
 */
@Service
@Slf4j
public class AcuityEncryptionService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public String encodePassword(String password){
        return bCryptPasswordEncoder.encode(password);
    }

    public boolean comparePassword(String hash, String password){
        return bCryptPasswordEncoder.matches(password, hash);
    }
}
