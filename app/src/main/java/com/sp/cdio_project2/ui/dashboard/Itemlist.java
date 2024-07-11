package com.sp.cdio_project2.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sp.cdio_project2.Database.DatabaseHelper;
import com.sp.cdio_project2.ui.dashboard.Item;
import com.sp.cdio_project2.ui.dashboard.ItemAdapter;
import com.sp.cdio_project2.R;

import java.util.List;

public class Itemlist extends Fragment {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_target, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = new DatabaseHelper(getContext());

        // Add initial items to the database if not already present
        List<Item> itemList = db.getAllItems();
        if (itemList.isEmpty()) {
            db.addItem(new Item(0, "1 Micron Filter", "Description 1", 0));
            db.addItem(new Item(0, "10 Micron Filter", "Description 2", 0));
            db.addItem(new Item(0, "30 Micron Filter", "Description 3", 0));
            db.addItem(new Item(0, "40 Micron Filter", "Description 1", 0));
            db.addItem(new Item(0, "50 Micron Filter", "Description 2", 0));
            db.addItem(new Item(0, "S75 Filter bags 100um", "Description 3", 0));
            db.addItem(new Item(0, "S75 Filter bags 150um", "Description 1", 0));
            db.addItem(new Item(0, "S75 Filter bags 200um", "Description 2", 0));
            db.addItem(new Item(0, "S50 Filter bags 150u", "Description 3", 0));
            db.addItem(new Item(0, "50 Filter Bag", "Description 1", 0));
            db.addItem(new Item(0, "V40 Dust Bag", "Description 2", 0));
            db.addItem(new Item(0, "V40 Hepa Filter(Current)", "Description 3", 0));
            db.addItem(new Item(0, "V40 Hepa Filter", "Description 1", 0));
            db.addItem(new Item(0, "Scrub 50 Pad holder", "Description 2", 0));
            db.addItem(new Item(0, "Phantas Dust Bag", "Description 3", 0));
            db.addItem(new Item(0, "50 Filter Bag", "Description 1", 0));
            itemList = db.getAllItems();
        }

        itemAdapter = new ItemAdapter(getContext(), itemList);
        recyclerView.setAdapter(itemAdapter);

        return view;
    }
}
