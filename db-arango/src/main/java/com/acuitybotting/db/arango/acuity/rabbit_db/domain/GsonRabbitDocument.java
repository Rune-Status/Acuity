package com.acuitybotting.db.arango.acuity.rabbit_db.domain;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
@Getter
@Setter
@ToString
public class GsonRabbitDocument extends RabbitDocumentBase {

    private JsonElement subDocument;
}
