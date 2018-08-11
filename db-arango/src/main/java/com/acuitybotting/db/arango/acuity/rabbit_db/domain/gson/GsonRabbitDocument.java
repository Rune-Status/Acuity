package com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.RabbitDocumentBase;
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
        T result = new Gson().fromJson(subDocument, type);
        if (result != null && result instanceof InheritSubId) ((InheritSubId) result).setParentSubId(getSubKey());
        return result;
    }
}
