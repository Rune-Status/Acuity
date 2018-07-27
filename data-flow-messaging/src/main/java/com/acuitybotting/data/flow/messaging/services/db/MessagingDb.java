package com.acuitybotting.data.flow.messaging.services.db;

import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.db.domain.Document;

/**
 * Created by Zachary Herridge on 7/26/2018.
 */
public interface MessagingDb {

    void update(String documentGroup, String key, String document) throws MessagingException;

    void update(String documentGroup, String key, String rev, String document) throws MessagingException;

    void save(String documentGroup, String key, String document) throws MessagingException;

    void save(String documentGroup, String key, String rev, String document) throws MessagingException;

    void upsert(String documentGroup, String key, int strategy, String insertDocument, String updateDocument) throws MessagingException;

    void upsert(String documentGroup, String key, String rev, int strategy, String insertDocument, String updateDocument) throws MessagingException;

    Document[] findAllByGroup(String documentGroup) throws MessagingException;

    Document findByGroupAndKey(String documentGroup, String key) throws MessagingException;
}
