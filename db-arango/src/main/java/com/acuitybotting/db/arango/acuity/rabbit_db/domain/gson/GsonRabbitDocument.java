package com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.RabbitDocumentBase;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.RabbitSubDocument;
import com.google.gson.Gson;
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

    public <T> T getSubDocumentAs(Class<T> type) {
        if (subDocument == null) return null;
        T result = new Gson().fromJson(subDocument, type);
        if (result instanceof RabbitSubDocument) ((RabbitSubDocument) result).setParent(this);
        return result;
    }
}
