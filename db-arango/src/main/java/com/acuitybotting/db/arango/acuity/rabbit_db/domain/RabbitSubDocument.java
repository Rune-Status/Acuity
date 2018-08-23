package com.acuitybotting.db.arango.acuity.rabbit_db.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Zachary Herridge on 8/23/2018.
 */
@Getter
@Setter
@ToString
public class RabbitSubDocument {

    private RabbitDocumentBase parent;

}
