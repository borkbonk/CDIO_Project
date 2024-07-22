package com.sp.cdio_project2.ui.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sp.cdio_project2.Database.DatabaseHelper;
import com.sp.cdio_project2.R;

import java.util.List;

public class Itemlist extends Fragment {

    private static final int EDIT_ITEM_REQUEST = 1;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private DatabaseHelper db;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_target, container, false);

     //   Toolbar toolbar = view.findViewById(R.id.toolbar);
//        AppCompatActivity activity = (AppCompatActivity) getActivity();
//     if (activity != null) {
//            activity.setSupportActionBar(toolbar);
//        }
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = new DatabaseHelper(getContext());

        List<Item> itemList = db.getAllItems();
        if (itemList.isEmpty()) {
            addInitialItemsToDatabase();
            itemList = db.getAllItems();
        }

        itemAdapter = new ItemAdapter(getContext(), itemList);
        recyclerView.setAdapter(itemAdapter);

        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_ITEM_REQUEST && resultCode == Activity.RESULT_OK) {
            // Refresh the item list
            List<Item> updatedItemList = db.getAllItems();
            itemAdapter.updateItems(updatedItemList);
        }
    }

    private void addInitialItemsToDatabase() {
        db.addItem(new Item(0, "1 Micron Filter", "Description 1", 0));
        db.addItem(new Item(1, "10 Micron Filter", "Description 2", 0));
        db.addItem(new Item(2, "30 Micron Filter", "Description 3", 0));
        db.addItem(new Item(3, "40 Micron Filter", "Description 1", 0));
        db.addItem(new Item(4, "50 Micron Filter", "Description 2", 0));
        db.addItem(new Item(5, "S75 Filter bags 100um", "Description 3", 0));
        db.addItem(new Item(6, "S75 Filter bags 150um", "Description 1", 0));
        db.addItem(new Item(7, "S75 Filter bags 200um", "Description 2", 0));
        db.addItem(new Item(8, "S50 Filter bags 150u", "Description 3", 0));
        db.addItem(new Item(9, "50 Filter Bag", "Description 1", 0));
        db.addItem(new Item(10, "V40 Dust Bag", "Description 2", 0));
        db.addItem(new Item(11, "V40 Hepa Filter(Current)", "Description 3", 0));
        db.addItem(new Item(12, "V40 Hepa Filter", "Description 1", 0));
        db.addItem(new Item(13, "Scrub 50 Pad holder", "Description 2", 0));
        db.addItem(new Item(14, "Phantas Dust Bag", "Description 3", 0));
        db.addItem(new Item(15, "50 Filter Bag", "Description 1", 0));
    }
}