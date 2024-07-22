package com.sp.cdio_project2.form;

// InvoiceDisplayActivity.java


import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.sp.cdio_project2.R;

public class InvoiceDisplayActivity extends AppCompatActivity {

    private TextView tvInvoiceDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_display);

        tvInvoiceDetails = findViewById(R.id.tvInvoiceDetails);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String customerName = extras.getString("customerName");
            String invoiceDate = extras.getString("invoiceDate");
            String itemDescription = extras.getString("itemDescription");
            int itemQuantity = extras.getInt("itemQuantity");
            double itemPrice = extras.getDouble("itemPrice");

            String invoiceDetails = "Customer Name: " + customerName + "\n"
                    + "Invoice Date: " + invoiceDate + "\n"
                    + "Item Description: " + itemDescription + "\n"
                    + "Item Quantity: " + itemQuantity + "\n"
                    + "Item Price: $" + itemPrice;

            tvInvoiceDetails.setText(invoiceDetails);
        }
    }
}

