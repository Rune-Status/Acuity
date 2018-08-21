package com.acuitybotting.db.arango.acuity.rabbit_db.service;

/**
 * Created by Zachary Herridge on 8/21/2018.
 */
public class RabbitDbAccess {

    public static boolean isDeleteAccessible(String userId, String db) {
        if (db == null) return false;
        if (db.equals("services.registered-connections")) return false;
        if (userId.equals("acuity-guest")) return false;
        return db.startsWith("services.") || db.startsWith("user.db.");
    }

    public static boolean isWriteAccessible(String userId, String db) {
        if (db == null) return false;
        return db.startsWith("services.") || db.startsWith("user.db.");
    }

    public static boolean isReadAccessible(String userId, String db) {
        if (db == null) return false;
        if (userId.equals("acuity-guest")) return false;
        return db.startsWith("services.") || db.startsWith("user.db.");
    }
}
