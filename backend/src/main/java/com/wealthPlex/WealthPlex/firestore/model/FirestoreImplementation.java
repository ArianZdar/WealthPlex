package com.wealthPlex.WealthPlex.firestore.model;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Configuration
public class FirestoreImplementation {

    private final String serviceAcccountPath = System.getenv("WEALTHPLEX_APPLICATION_CREDENTIALS");
    private final String projectId = System.getenv("WEALTHPLEX_PROJECTID");
    private Firestore firestore = null;

    private void init() {
        try {
            FileInputStream serviceAccount = new FileInputStream(serviceAcccountPath);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(projectId)
                    .build();
            FirebaseApp.initializeApp(options);
            this.firestore = FirestoreClient.getFirestore();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void setFirestore(Firestore firestore) {
        this.firestore = firestore;
    }

    public Firestore getFirestore() {
        if (firestore == null) {
            init();
        }
        return firestore;
    }

    public DocumentReference getDocumentReference(String collectionName, String documentId) {
        return getFirestore().collection(collectionName).document(documentId);
    }

    public CollectionReference getCollectionReference(String collectionName) {
        return getFirestore().collection(collectionName);
    }

    public DocumentSnapshot getDocument(String collectionName, String documentId) {
        DocumentReference documentReference = getCollectionReference(collectionName).document(documentId);
        return  getDocumentFromReference(documentReference);
    }

    public DocumentSnapshot getDocumentFromReference(DocumentReference reference) {
        ApiFuture<DocumentSnapshot> future = reference.get();
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public DocumentReference addDocumentToCollection(String collectionName, Map<String, Object> data) throws FileNotFoundException {
        Iterator<CollectionReference> collections = getFirestore().listCollections().iterator();
        boolean found = false;

        while (collections.hasNext()) {
            CollectionReference collection = collections.next();
            if (collection.getId().equals(collectionName)) found = true;
        }

        if (!found) throw new FileNotFoundException("Collection " + collectionName + " does not exist!");
        return addDocumentToNewCollection(collectionName, data);
    }

    public DocumentReference addDocumentToNewCollection(String collectionName, Map<String, Object> data) {
        ApiFuture<DocumentReference> addedDocumentReference = getFirestore().collection(collectionName).add(data);
        try {
            return addedDocumentReference.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public DocumentReference addDocumentToCollectionWithId(String collectionName, Map<String, Object> data, String id) {
        ApiFuture<WriteResult> writeResult = getFirestore().collection(collectionName).document(id).set(data);
        WriteResult result = null;
        try {
            result = writeResult.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return getFirestore().collection(collectionName).document(id);
    }

    public WriteResult updateDocument(String collectionName, String documentId, Map<String, Object> data) {
        ApiFuture<WriteResult> updateResult = getFirestore().collection(collectionName).document(documentId).set(data);
        try {
            return updateResult.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public WriteResult updateDocumentField(String collectionName, String documentId, String fieldName, Object value) {
        ApiFuture<WriteResult> updateResult = getFirestore().collection(collectionName).document(documentId).update(fieldName, value);
        try {
            return updateResult.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public WriteResult deleteDocument(String collectionName, String documentId) {
        ApiFuture<WriteResult> deleteResult = getFirestore().collection(collectionName).document(documentId).delete();
        try {
            return deleteResult.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }
}
