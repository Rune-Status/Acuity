package com.acuitybotting.db.arangodb.repositories.resources.configurations;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Zachary Herridge on 8/6/2018.
 */
@Getter
@Setter
@ToString
public class ClientConfiguration {

    private JsonObject settings;
}
