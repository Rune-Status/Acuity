package com.acuitybotting.db.arangodb.repositories.resources.proxies.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Proxy {

    private String _key;

    private String principalId;

    private String host;
    private String port;
    private String username;
    private String encryptedPassword;
}
