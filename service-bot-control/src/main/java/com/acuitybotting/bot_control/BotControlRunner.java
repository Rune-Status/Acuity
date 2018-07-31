package com.acuitybotting.bot_control;

import com.acuitybotting.bot_control.services.user.db.RabbitDbService;
import com.acuitybotting.data.flow.messaging.services.db.domain.RabbitDbRequest;
import com.acuitybotting.db.arango.acuity.bot_control.domain.RabbitDocument;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 7/25/2018.
 */
@Component
@Slf4j
public class BotControlRunner implements CommandLineRunner {

    private final RabbitDbService rabbitDbService;

    @Autowired
    public BotControlRunner(RabbitDbService rabbitDbService) {
        this.rabbitDbService = rabbitDbService;
    }

    private Map<String, Set<Xtea>> findUniqueAfter() {
        RabbitDbRequest rabbitDbRequest = new RabbitDbRequest();
        Set<RabbitDocument> server1 = rabbitDbService.loadByGroup("server", rabbitDbRequest.setDatabase("services.xteas").setGroup("region-xteas"));
        return server1.stream().map(rabbitDocument -> new Gson().fromJson(rabbitDocument.getSubDocument(), Xtea.class)).collect(Collectors.groupingBy(object -> String.valueOf(object.getRegion()), Collectors.toSet()));
    }

    public void exportXteasGreaterThanRev(int rev, File out) {
        Map<String, Set<Xtea>> uniqueAfter = findUniqueAfter();
        Set<Map.Entry<String, Set<Xtea>>> keySets = uniqueAfter.entrySet();

        StringJoiner stringJoiner = new StringJoiner("\n");
        for (Map.Entry<String, Set<Xtea>> keySetEntry : keySets) {
            StringBuilder result = new StringBuilder(keySetEntry.getKey());
            for (Xtea xtea : keySetEntry.getValue()) {
                result.append(" ").append(Arrays.stream(xtea.getKeys()).mapToObj(String::valueOf).collect(Collectors.joining(",")));
            }
            stringJoiner.add(result.toString());
        }
        log.info("Exported {} xteas to file {}.", keySets.size(), out);

        try {
            Files.write(new File(out, "xteas.txt").toPath(), stringJoiner.toString().getBytes());
            Files.write(new File(out, "xteas.json").toPath(), new Gson().toJson(uniqueAfter).getBytes());
        } catch (IOException e) {
            log.error("Error during exporting xteas.", e);
        }
    }

    @Override
    public void run(String... strings) throws Exception {
  /*      exportXteasGreaterThanRev(12, new File("C:\\Users\\zgher\\Pathing\\xteas\\"));
        System.out.println("Done");*/
    }

    public class Xtea {

        @Id
        private String id;

        private int revision;
        private int region;
        private int[] keys;

        public int getRevision() {
            return revision;
        }

        public int getRegion() {
            return region;
        }

        public String getId() {
            return id;
        }

        public int[] getKeys() {
            return keys;
        }
    }


}
