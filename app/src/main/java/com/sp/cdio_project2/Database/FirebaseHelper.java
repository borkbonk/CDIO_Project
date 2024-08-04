package com.sp.cdio_project2.Database;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sp.cdio_project2.ui.home.PdfMetadata;
import com.sp.cdio_project2.ui.dashboard.Item;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    private static final String COLLECTION_ITEMS = "items";
    private static final String COLLECTION_PDFS = "pdf_metadata"; // Updated to match collection name in Firestore
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;

    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void addItem(Item item, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection(COLLECTION_ITEMS).add(item)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void getItem(String id, final MutableLiveData<Item> itemLiveData, OnFailureListener onFailureListener) {
        db.collection(COLLECTION_ITEMS).document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Item item = documentSnapshot.toObject(Item.class);
                        item.setId(documentSnapshot.getId());
                        itemLiveData.setValue(item);
                    } else {
                        itemLiveData.setValue(null);
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

    public MutableLiveData<List<Item>> getAllItems(MutableLiveData<List<Item>> itemListLiveData, OnFailureListener onFailureListener) {
        listenerRegistration = db.collection(COLLECTION_ITEMS).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                onFailureListener.onFailure(e);
                return;
            }

            List<Item> itemList = new ArrayList<>();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Item item = doc.toObject(Item.class);
                item.setId(doc.getId());
                itemList.add(item);
            }

            itemListLiveData.setValue(itemList);
        });
        return itemListLiveData;
    }

    public void updateItem(String itemId, Item item, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection(COLLECTION_ITEMS).document(itemId).set(item)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }
    public void updatePdfMetadataWithId(String documentId, PdfMetadata metadata) {
        db.collection(COLLECTION_PDFS).document(documentId).set(metadata)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "PDF metadata updated with document ID: " + documentId))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating PDF metadata with ID: ", e));
    }

    public void addPdfMetadata(PdfMetadata pdfMetadata, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection(COLLECTION_PDFS).add(pdfMetadata)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "PDF metadata added with ID: " + documentReference.getId());
                    onSuccessListener.onSuccess(documentReference);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding PDF metadata: ", e);
                    onFailureListener.onFailure(e);
                });
    }

    public MutableLiveData<List<PdfMetadata>> getAllPdfs() {
        MutableLiveData<List<PdfMetadata>> pdfMetadataLiveData = new MutableLiveData<>();
        db.collection(COLLECTION_PDFS).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Error fetching PDF metadata", e);
                return;
            }

            if (queryDocumentSnapshots != null) {
                List<PdfMetadata> pdfMetadataList = new ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    PdfMetadata metadata = doc.toObject(PdfMetadata.class);
                    pdfMetadataList.add(metadata);
                }
                pdfMetadataLiveData.setValue(pdfMetadataList);
            } else {
                pdfMetadataLiveData.setValue(new ArrayList<>());
            }
        });
        return pdfMetadataLiveData;
    }

    public Task<Void> deletePdfMetadata(String documentId) {
        return db.collection(COLLECTION_PDFS).document(documentId).delete();
    }

    public void removeListener() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    public String getNewItemId() {
        return db.collection(COLLECTION_ITEMS).document().getId();
    }
}
