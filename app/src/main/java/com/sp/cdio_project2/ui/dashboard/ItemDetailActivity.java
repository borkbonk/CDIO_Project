package com.sp.cdio_project2.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.sp.cdio_project2.Database.FirebaseHelper;
import com.sp.cdio_project2.R;

public class ItemDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private EditText quantityEditText;
    private Button decreaseButton;
    private Button increaseButton;
    private Button saveButton;

    private FirebaseHelper firebaseHelper;
    private Item currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        quantityEditText = findViewById(R.id.quantityEditText);
        decreaseButton = findViewById(R.id.decreaseButton);
        increaseButton = findViewById(R.id.increaseButton);
        saveButton = findViewById(R.id.saveButton);

        firebaseHelper = new FirebaseHelper();

        Intent intent = getIntent();
        String itemId = intent.getStringExtra("ITEM_ID");
        if (itemId != null) {
            loadItem(itemId);
        }

        decreaseButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(quantityEditText.getText().toString());
            if (currentQuantity > 0) {
                quantityEditText.setText(String.valueOf(currentQuantity - 1));
            }
        });

        increaseButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(quantityEditText.getText().toString());
            quantityEditText.setText(String.valueOf(currentQuantity + 1));
        });

        saveButton.setOnClickListener(v -> {
            int newQuantity = Integer.parseInt(quantityEditText.getText().toString());
            currentItem.setQuantity(newQuantity);
            updateItemInFirebase(currentItem);
            setResult(RESULT_OK);
            finish();
        });
    }

    private void loadItem(String itemId) {
        MutableLiveData<Item> itemLiveData = new MutableLiveData<>();
        firebaseHelper.getItem(itemId, itemLiveData, e -> {
            // Handle error
        });

        itemLiveData.observe(this, item -> {
            if (item != null) {
                currentItem = item;
                updateUI();
            }
        });
    }

    private void updateUI() {
        titleTextView.setText(currentItem.getTitle());
        descriptionTextView.setText(currentItem.getDescription());
        quantityEditText.setText(String.valueOf(currentItem.getQuantity()));
    }

    private void updateItemInFirebase(Item item) {
        firebaseHelper.updateItem(item.getId(), item, unused -> {
            // Successfully updated item in Firebase
        }, e -> {
            // Handle error
        });
    }
}
