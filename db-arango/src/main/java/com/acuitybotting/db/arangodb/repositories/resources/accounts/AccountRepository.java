package com.acuitybotting.db.arangodb.repositories.resources.accounts;

import com.acuitybotting.db.arangodb.api.query.Aql;
import com.acuitybotting.db.arangodb.api.repository.ArangoRepository;
import com.acuitybotting.db.arangodb.api.services.ArangoDbService;
import com.acuitybotting.db.arangodb.repositories.resources.accounts.domain.RsAccount;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class AccountRepository extends ArangoRepository<RsAccount> {

    protected AccountRepository(ArangoDbService arangoDbService) {
        super(RsAccount.class, arangoDbService);
    }

    @Override
    public Stream<RsAccount> findByFields(Object... filters) {
        return execute(Aql.findByFields(filters).withRelativeReference("bank", "_bank")).stream();
    }

    @Override
    public String getCollectionName() {
        return "resources-accounts";
    }
}
