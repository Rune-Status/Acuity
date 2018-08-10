package com.acuitybotting.security.acuity.encryption;

import com.rockaport.alice.Alice;
import com.rockaport.alice.AliceContext;
import com.rockaport.alice.AliceContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;


/**
 * Created by Zachary Herridge on 8/9/2018.
 */
@Service
@Slf4j
public class AcuityEncryptionService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private Alice getAlice(){
        return new Alice(new AliceContextBuilder().setKeyLength(AliceContext.KeyLength.BITS_128).build());
    }

    public String encrypt(String key, String value) throws GeneralSecurityException {
        return Base64.encodeBase64String(getAlice().encrypt(value.getBytes(), key.toCharArray()));
    }

    public String decrypt(String key, String value) throws GeneralSecurityException {
        return new String(getAlice().decrypt(Base64.decodeBase64(value), key.toCharArray()));
    }

    public String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    public boolean comparePassword(String hash, String password) {
        return bCryptPasswordEncoder.matches(password, hash);
    }
}
