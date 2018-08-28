package com.acuitybotting.db.arangodb.api.services;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ArangoDbService {

    @Value("${arango.host}")
    private String host;

    @Value("${arango.port}")
    private int port;

    @Value("${arango.username}")
    private String username;

    @Value("${arango.password}")
    private String password;

    private ArangoDB driver;

    @PostConstruct
    private void init(){
        driver = buildDriver();
    }

    private ArangoDB buildDriver(){
        return new ArangoDB.Builder()
                .user(username)
                .password(password)
                .host(host, port)
                .build();
    }

    public ArangoDB getDriver() {
        return driver;
    }

    public ArangoDatabase getDefaultDb(){
        return getDriver().db("AcuityBotting-1");
    }
}
