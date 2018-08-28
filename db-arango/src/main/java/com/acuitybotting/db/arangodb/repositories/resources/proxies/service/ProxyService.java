package com.acuitybotting.db.arangodb.repositories.resources.proxies.service;

import com.acuitybotting.db.arangodb.repositories.resources.proxies.ProxyRepository;
import com.acuitybotting.db.arangodb.repositories.resources.proxies.domain.Proxy;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Getter
public class ProxyService {

    private ProxyRepository repository;

    public Set<Proxy> loadProxies(String principalId){
        return repository.findByFields("principalId", principalId).collect(Collectors.toSet());
    }

}
