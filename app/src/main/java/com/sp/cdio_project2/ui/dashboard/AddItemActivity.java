package com.sp.cdio_project2.ui.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.sp.cdio_project2.Database.FirebaseHelper;
import com.sp.cdio_project2.R;
import com.sp.cdio_project2.ui.dashboard.Item;

public class AddItemActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText, codeEditText, quantityEditText;
    private Button addItemButton;
    private ProgressBar progressBar;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        codeEditText = findViewById(R.id.codeEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        addItemButton = findViewById(R.id.addItemButton);
        progressBar = findViewById(R.id.progressBar);
        firebaseHelper = new FirebaseHelper();

        addItemButton.setOnClickListener(v -> addItem());
    }

    private void addItem() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String code = codeEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(code) || TextUtils.isEmpty(quantityString)) {
            return;
        }

        int quantity = Integer.parseInt(quantityString);
        String id = firebaseHelper.getNewItemId(); // Generate a new unique ID for the item

        Item newItem = new Item(id, title, description, code, quantity);

        progressBar.setVisibility(View.VISIBLE);
        firebaseHelper.addItem(newItem, docRef -> {
            progressBar.setVisibility(View.GONE);
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }, e -> {
            progressBar.setVisibility(View.GONE);
            // Handle error
        });
    }
}
