package com.sp.cdio_project2.form;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.sp.cdio_project2.Database.DatabaseHelper;
import com.sp.cdio_project2.R;
import com.sp.cdio_project2.ui.dashboard.Item;

import java.util.List;

public class Invoiceform extends Fragment {

    private DatabaseHelper db;
    private Spinner itemSpinner;
    private EditText quantityEditText;
    private Button generateOrderButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_invoiceform, container, false);

        db = new DatabaseHelper(getContext());

        itemSpinner = view.findViewById(R.id.itemSpinner);
        quantityEditText = view.findViewById(R.id.quantityEditText);
        generateOrderButton = view.findViewById(R.id.generateOrderButton);

        loadItems();

        generateOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateOrder();
            }
        });

        return view;
    }

    private void loadItems() {
        List<Item> items = db.getAllItems();
        ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSpinner.setAdapter(adapter);
    }

    private void generateOrder() {
        Item selectedItem = (Item) itemSpinner.getSelectedItem();
        String quantityStr = quantityEditText.getText().toString().trim();

        if (selectedItem == null || TextUtils.isEmpty(quantityStr)) {
            Toast.makeText(getContext(), "Please select an item and enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);

        if (quantity <= 0) {
            Toast.makeText(getContext(), "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        int currentQuantity = selectedItem.getQuantity();
        if (quantity > currentQuantity) {
            Toast.makeText(getContext(), "Insufficient stock available", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedItem.setQuantity(currentQuantity - quantity);
        db.updateItem(selectedItem);

        Toast.makeText(getContext(), "Order generated successfully", Toast.LENGTH_SHORT).show();
    }
}
