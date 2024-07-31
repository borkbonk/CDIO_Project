package com.sp.cdio_project2.Database;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private static final String COLLECTION_PDFS = "pdfs";
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

    public void removeListener() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
    public String getNewItemId() {
        return db.collection(COLLECTION_ITEMS).document().getId();
    }
}
