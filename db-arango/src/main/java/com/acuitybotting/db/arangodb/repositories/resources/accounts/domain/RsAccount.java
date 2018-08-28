package com.acuitybotting.db.arangodb.repositories.resources.accounts.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RsAccount {

    private String _key;

    private String encryptedPassword;

    private int world;

    private RsAccountStats stats;

    private RsItemTable inventory;
    private RsItemTable bank;
    private RsItemTable equipment;
}
