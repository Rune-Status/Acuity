package com.acuitybotting.db.arangodb.repositories.resources.proxies;

import com.acuitybotting.db.arangodb.api.repository.ArangoRepository;
import com.acuitybotting.db.arangodb.api.services.ArangoDbService;
import com.acuitybotting.db.arangodb.repositories.resources.proxies.domain.Proxy;
import org.springframework.stereotype.Service;

@Service
public class ProxyRepository extends ArangoRepository<Proxy> {

    protected ProxyRepository(ArangoDbService arangoDbService) {
        super(Proxy.class, arangoDbService);
    }

    @Override
    public String getCollectionName() {
        return "proxy";
    }
}
