package com.sp.cdio_project2.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sp.cdio_project2.Database.DatabaseHelper;
import com.sp.cdio_project2.R;

public class ItemDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private EditText quantityEditText;
    private Button decreaseButton;
    private Button increaseButton;
    private Button saveButton;

    private DatabaseHelper db;
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

        db = new DatabaseHelper(this);

        Intent intent = getIntent();
        int itemId = intent.getIntExtra("ITEM_ID", -1);
        if (itemId != -1) {
            currentItem = db.getItem(itemId);
            updateUI();
        }

        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(quantityEditText.getText().toString());
                if (currentQuantity > 0) {
                    quantityEditText.setText(String.valueOf(currentQuantity - 1));
                }
            }
        });

        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(quantityEditText.getText().toString());
                quantityEditText.setText(String.valueOf(currentQuantity + 1));
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = Integer.parseInt(quantityEditText.getText().toString());
                currentItem.setQuantity(newQuantity);
                db.updateItem(currentItem);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void updateUI() {
        titleTextView.setText(currentItem.getTitle());
        descriptionTextView.setText(currentItem.getDescription());
        quantityEditText.setText(String.valueOf(currentItem.getQuantity()));
    }
}