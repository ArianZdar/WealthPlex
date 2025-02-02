package com.wealthPlex.WealthPlex.core.repositories;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.wealthPlex.WealthPlex.core.models.DocumentData;
import com.wealthPlex.WealthPlex.firestore.model.FirestoreImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;


public abstract class DocumentRepository {

    @Autowired
    private FirestoreImplementation firestoreImplementation;

    public String collectionName;

    public abstract Map<String, Object> getAsMap(DocumentData data);
    public abstract DocumentData getFromMap(Map<String, Object> map);


    public WriteResult deleteDocument(DocumentData data) {
        return firestoreImplementation.deleteDocument(collectionName, data.getId());
    }

    public DocumentData getDocumentById(String id) {
        DocumentSnapshot document = firestoreImplementation.getDocument(collectionName,id);
        Map<String, Object> dataMap = document.getData();
        dataMap.put("id", id);
        DocumentData data = getFromMap(dataMap);
        return data;
    }

    public abstract DocumentReference saveDocument(DocumentData data);

    public DocumentReference saveDocumentWithId(String id, DocumentData data) {
        DocumentReference documentReference = firestoreImplementation.addDocumentToCollectionWithId(collectionName,getAsMap(data),id);
        return documentReference;
    }

    public WriteResult updateDocument(DocumentData data) {
        return firestoreImplementation.updateDocument(collectionName,data.getId(),getAsMap(data));
    }

    public WriteResult updateDocumentField(DocumentData data, String field, Object value) {
        Map<String, Object> map = getAsMap(data);
        if (getAsMap(data).containsKey(field)) {
            map.put(field, value);
        } else {
            throw new IllegalArgumentException("Field " + field + " not found");
        }
        return firestoreImplementation.updateDocumentField(collectionName,data.getId(),field,value);
    }

    public String getNewId() {
        return firestoreImplementation.getFirestore().collection(collectionName).document().getId();
    }

    public boolean idAvailable(String id,String collectionName) {
        DocumentReference documentReference = firestoreImplementation.getDocumentReference(collectionName,id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document;
        try {
            document = future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return !document.exists();
    }
}
