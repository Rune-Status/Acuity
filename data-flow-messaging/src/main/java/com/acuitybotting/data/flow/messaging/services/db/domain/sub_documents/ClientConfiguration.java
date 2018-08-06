package com.acuitybotting.data.flow.messaging.services.db.domain.sub_documents;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * Created by Zachary Herridge on 8/6/2018.
 */
@Getter
@Setter
@ToString
public class ClientConfiguration {

    private Map<String, Object> settings;
}
