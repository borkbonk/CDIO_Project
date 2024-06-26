package com.sp.cdio_project2.form;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.sp.cdio_project2.Database.DatabaseHelper;
import com.sp.cdio_project2.R;

public class form_ extends Fragment {

    private EditText titleEditText, descriptionEditText;
    private Button saveButton;
    private LinearLayout cardContainer;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.form, container, false);

        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        saveButton = view.findViewById(R.id.saveButton);
        cardContainer = view.findViewById(R.id.cardContainer);
        databaseHelper = new DatabaseHelper(getContext());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                if (!title.isEmpty() && !description.isEmpty()) {
                    databaseHelper.addData(title, description);
                    titleEditText.setText("");
                    descriptionEditText.setText("");
                    loadCards();
                }
            }
        });

        loadCards();
        return view;
    }

    private void loadCards() {
        cardContainer.removeAllViews();
        Cursor cursor = databaseHelper.getAllData();
        while (cursor.moveToNext()) {
            String title = cursor.getString(1);
            String description = cursor.getString(2);
            addCardView(title, description);
        }
    }

    private void addCardView(String title, String description) {
        CardView cardView = new CardView(getContext());
        cardView.setLayoutParams(new LinearLayout.LayoutParams(
                300, 400));
        cardView.setRadius(8);
        cardView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.card_gradient));
        cardView.setContentPadding(16, 16, 16, 16);

        // Add content to the CardView
        RelativeLayout cardLayout = new RelativeLayout(getContext());
        cardLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        TextView titleTextView = new TextView(getContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(18);
        titleTextView.setTextColor(Color.WHITE);

        TextView descriptionTextView = new TextView(getContext());
        descriptionTextView.setText(description);
        descriptionTextView.setTextSize(14);
        descriptionTextView.setTextColor(Color.WHITE);

        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        RelativeLayout.LayoutParams descriptionParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        descriptionParams.addRule(RelativeLayout.BELOW, titleTextView.getId());
        descriptionParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        cardLayout.addView(titleTextView, titleParams);
        cardLayout.addView(descriptionTextView, descriptionParams);

        cardView.addView(cardLayout);

        cardContainer.addView(cardView);
    }
}
