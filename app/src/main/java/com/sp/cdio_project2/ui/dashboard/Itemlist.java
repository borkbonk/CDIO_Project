package com.sp.cdio_project2.ui.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.sp.cdio_project2.Database.FirebaseHelper;
import com.sp.cdio_project2.R;
import java.util.List;

public class Itemlist extends Fragment {

    private static final int EDIT_ITEM_REQUEST = 1;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private FirebaseHelper firebaseHelper;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_target, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseHelper = new FirebaseHelper();

        // Reinitialize Firestore collection with new items and fetch them
        reinitializeFirestoreCollection(() -> fetchDataFromFirebase());

        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (itemAdapter != null) {
                    itemAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        return view;
    }

    private void fetchDataFromFirebase() {
        firebaseHelper.getAllItems(new MutableLiveData<>(), e -> {
            // Handle error
        }).observe(getViewLifecycleOwner(), items -> {
            if (items != null && items.size() > 0) {
                itemAdapter = new ItemAdapter(getContext(), items);
                recyclerView.setAdapter(itemAdapter);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_ITEM_REQUEST && resultCode == Activity.RESULT_OK) {
            // Refresh the item list from Firebase
            fetchDataFromFirebase();
        }
    }

    private void reinitializeFirestoreCollection(@NonNull Runnable callback) {
        // Add initial items to Firestore
        firebaseHelper.addItem(new Item("1", "1 Micron Filter", "A0607010010 Description", "A0607010010", 0), docRef -> callback.run(), e -> callback.run());
        firebaseHelper.addItem(new Item("2", "10 Micron Filter", "A0607010009 Description", "A0607010009", 0), docRef -> callback.run(), e -> callback.run());
        firebaseHelper.addItem(new Item("3", "30 Micron Filter", "A0607010007 Description", "A0607010007", 0), docRef -> callback.run(), e -> callback.run());
        firebaseHelper.addItem(new Item("4", "40 Micron Filter", "A0306010006 Description", "A0306010006", 0), docRef -> callback.run(), e -> callback.run());
        firebaseHelper.addItem(new Item("5", "50 Micron Filter", "Nil Description", "Nil-", 0), docRef -> callback.run(), e -> callback.run());
        firebaseHelper.addItem(new Item("6", "S75 Filter bags 100um", "A0607010002 Description", "A0607010002", 0), docRef -> callback.run(), e -> callback.run());
        firebaseHelper.addItem(new Item("7", "S75 Filter bags 150um", "A0607010003 Description", "A0607010003", 0), docRef -> callback.run(), e -> callback.run());
        firebaseHelper.addItem(new Item("8", "S75 Filter bags 200um", "Nil Description", "Nil-", 0), docRef -> callback.run(), e -> callback.run());
        firebaseHelper.addItem(new Item("9", "S50 Filter bags 150u", "A0303010598 Description", "A0303010598", 0), docRef -> callback.run(), e -> callback.run());
        firebaseHelper.addItem(new Item("10", "50 Filter Bag", "Nil Description", "Nil", 0), docRef -> callback.run(), e -> callback.run());
        firebaseHelper.addItem(new Item("11", "V40 Dust Bag", "A0313400051 Description", "A0313400051", 0), docRef -> callback.run(), e -> callback.run());
        firebaseHelper.addItem(new Item("12", "V40 Hepa Filter", "A0313400373 Description", "A0313400373", 0), docRef -> callback.run(), e -> callback.run());
        firebaseHelper.addItem(new Item("13", "V40 Hepa Filter", "A0313400371 Description", "A0313400371", 0), docRef -> callback.run(), e -> callback.run());
        firebaseHelper.addItem(new Item("14", "Scrub 50 Pad holder", "Nil Description", "Nil", 0), docRef -> callback.run(), e -> callback.run());
        firebaseHelper.addItem(new Item("15", "Phantas Dust Bag", "A0306000031A Description", "A0306000031A", 0), docRef -> callback.run(), e -> callback.run());
        firebaseHelper.addItem(new Item("16", "Scrub 50 Brush", "A0303010557 Description", "A0303010557", 0), docRef -> callback.run(), e -> callback.run());
    }

}
