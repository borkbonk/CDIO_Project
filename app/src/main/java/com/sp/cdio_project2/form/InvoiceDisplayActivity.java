package com.sp.cdio_project2.form;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.sp.cdio_project2.R;
import java.util.ArrayList;

public class InvoiceDisplayActivity extends AppCompatActivity {

    private TextView tvCustomerName;
    private TextView tvInvoiceDate;
    private TextView tvInvoiceId;
    private TextView tvInvoiceName;
    private TextView tvDateGenerated; // New TextView for Date of Generation
    private LinearLayout itemsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_display);

        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvInvoiceDate = findViewById(R.id.tvInvoiceDate);
        tvInvoiceId = findViewById(R.id.tvInvoiceId);
        tvInvoiceName = findViewById(R.id.tvInvoiceName);
        tvDateGenerated = findViewById(R.id.tvDateGenerated); // Initialize the new TextView
        itemsContainer = findViewById(R.id.itemsContainer);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String customerName = extras.getString("customerName");
            String invoiceDate = extras.getString("invoiceDate");
            String invoiceId = extras.getString("invoiceId");
            String invoiceName = extras.getString("invoiceName");
            String dateGenerated = extras.getString("dateGenerated"); // Retrieve the date of generation
            ArrayList<String> itemDescriptions = extras.getStringArrayList("itemDescriptions");
            ArrayList<Integer> itemQuantities = extras.getIntegerArrayList("itemQuantities");

            tvCustomerName.setText(customerName);
            tvInvoiceDate.setText(invoiceDate);
            tvInvoiceName.setText(invoiceName);
            tvDateGenerated.setText(dateGenerated); // Set the date of generation

            for (int i = 0; i < itemDescriptions.size(); i++) {
                String itemDescription = itemDescriptions.get(i);
                int itemQuantity = itemQuantities.get(i);

                TextView itemDetailView = new TextView(this);
                itemDetailView.setText(itemDescription + " - Quantity: " + itemQuantity);
                itemsContainer.addView(itemDetailView);
            }
        }
    }
}
