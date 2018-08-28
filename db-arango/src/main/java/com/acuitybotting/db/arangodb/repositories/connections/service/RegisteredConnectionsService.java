package com.acuitybotting.db.arangodb.repositories.connections.service;

import com.acuitybotting.db.arangodb.api.query.Aql;
import com.acuitybotting.db.arangodb.api.query.AqlQuery;
import com.acuitybotting.db.arangodb.repositories.connections.RegisteredConnectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;


/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Service
@Slf4j
public class RegisteredConnectionsService {

    @Value("${rabbit.username}")
    private String username;
    @Value("${rabbit.password}")
    private String password;

    private final RegisteredConnectionRepository repository;

    @Autowired
    public RegisteredConnectionsService(RegisteredConnectionRepository repository) {
        this.repository = repository;
    }

    public void updateConnections() {
        AqlQuery query = Aql.query("FOR d IN @@collection")
                .append("LET connected = r._lastUpdateTime > @timeout")
                .append("UPDATE { _key: d._key, connected : connected} IN @@collection")
                .withParameter("timeout", System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(20));
        repository.execute(query);
    }
}
