package com.acuitybotting.test;

import com.acuitybotting.db.arangodb.api.query.Aql;
import com.acuitybotting.db.arangodb.api.services.ArangoDbService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.acuitybotting")
public class DbTestApplication implements CommandLineRunner {

    private final ArangoDbService arangoDbService;
    private final TestRepo testRepo;

    public DbTestApplication(ArangoDbService arangoDbService, TestRepo testRepo) {
        this.arangoDbService = arangoDbService;
        this.testRepo = testRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        testRepo.execute(
                Aql.upsertByKey(
                        "user1",
                        "{_key: @key, displayName: 'Zach', searches: 0}",
                        "{searches: OLD.searches + 1}"
                )).forEachRemaining(System.out::println);
    }

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(DbTestApplication.class);
        builder.run(args);
    }
}
