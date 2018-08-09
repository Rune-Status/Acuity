package com.acuitybotting.security.jwt.domain;


import lombok.Data;

import java.util.Objects;

/**
 * Created by Zachary Herridge on 6/6/2018.
 */
@Data
public class JwtPrincipal {

    private String username;
    private String email;
    private String sub;
    private String realm;
    private String[] roles;

    public String getPrincipalUid(){
        Objects.requireNonNull(sub);
        return sub;
    }
}
