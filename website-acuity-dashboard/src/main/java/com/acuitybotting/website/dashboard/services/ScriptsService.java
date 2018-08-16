package com.acuitybotting.website.dashboard.services;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zachary Herridge on 8/16/2018.
 */
@Service
@Slf4j
public class ScriptsService {

    private LoadingCache<String, Set<String>> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build(new CacheLoader<String, Set<String>>() {
                @Override
                public Set<String> load(String key) throws Exception {
                    if (key.equals("rspeer")) return loadRsPeerScript();
                    return Collections.emptySet();
                }
            });

    private Set<String> loadRsPeerScript() throws UnirestException {
        Set<String> scripts = new HashSet<>();
        JsonArray jsonElements = new Gson().fromJson(Unirest.get("http://api.rspeer.org/v2/scripts/allScripts").asString().getBody(), JsonArray.class);
        for (JsonElement jsonElement : jsonElements) {
            JsonObject script = jsonElement.getAsJsonObject();

            String name = script.get("name").getAsString();
            String author = script.get("author").getAsString();
            scripts.add(author + "/" + name);
        }
        return scripts;
    }

    public Set<String> getAllRsPeerScripts() {
        try {
            return cache.get("rspeer");
        } catch (ExecutionException e) {
            log.error("Error while loading rspeer scripts.", e);
        }
        return Collections.emptySet();
    }
}
