package com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.RabbitSubDocument;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class RsAccountInfo extends RabbitSubDocument {

    private String encryptedPassword;

    private int world;
    private Map<String, Long> levels = new HashMap<>();

    private Map<Integer, Integer> inventory = new HashMap<>();
    private Map<Integer, Integer> bank = new HashMap<>();
}
