package com.acuitybotting.db.arangodb.repositories.resources.accounts;

import com.acuitybotting.db.arangodb.api.repository.ArangoRepository;
import com.acuitybotting.db.arangodb.api.services.ArangoDbService;
import com.acuitybotting.db.arangodb.repositories.resources.accounts.domain.RsAccount;
import org.springframework.stereotype.Service;

@Service
public class AccountRepository extends ArangoRepository<RsAccount> {

    protected AccountRepository(ArangoDbService arangoDbService) {
        super(RsAccount.class, arangoDbService);
        setReferences(new String[]{"stats", "inventory", "bank", "equipment"});
    }

    @Override
    public String getCollectionName() {
        return "resources-accounts";
    }
}
