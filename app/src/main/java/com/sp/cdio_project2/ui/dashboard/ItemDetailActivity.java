package com.sp.cdio_project2.ui.dashboard;




import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sp.cdio_project2.Database.DatabaseHelper;
import com.sp.cdio_project2.R;

public class ItemDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private EditText quantityEditText;
    private Button saveButton;
    private DatabaseHelper db;
    private Item item;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        titleTextView = findViewById(R.id.item_detail_title);
        descriptionTextView = findViewById(R.id.item_detail_description);
        quantityEditText = findViewById(R.id.item_detail_quantity);
        saveButton = findViewById(R.id.item_detail_save_button);

        db = new DatabaseHelper(this);
        int itemId = getIntent().getIntExtra("item_id", -1);

        item = db.getItem(itemId);

        if (item != null) {
            titleTextView.setText(item.getTitle());
            descriptionTextView.setText(item.getDescription());
            quantityEditText.setText(String.valueOf(item.getQuantity()));
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = Integer.parseInt(quantityEditText.getText().toString());
                item.setQuantity(newQuantity);
                db.updateItem(item);
            }
        });
    }
}
