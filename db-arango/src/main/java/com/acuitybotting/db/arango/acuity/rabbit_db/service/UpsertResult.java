package com.acuitybotting.db.arango.acuity.rabbit_db.service;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Zachary Herridge on 8/21/2018.
 */
@Getter
@Setter
@ToString
public class UpsertResult {

    private GsonRabbitDocument previous;
    private GsonRabbitDocument current;

}
