package com.sp.cdio_project2.form;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.sp.cdio_project2.Database.DatabaseHelper;
import com.sp.cdio_project2.R;
import com.sp.cdio_project2.ui.dashboard.Item;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class Invoiceform extends Fragment {
    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private DatabaseHelper db;
    private LinearLayout itemsContainer;
    private Button addItemButton;
    private Button generateOrderButton;
    private List<View> itemViews;

    private EditText customerNameEditText;
    private EditText invoiceDateEditText;
    private EditText invoiceNameEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_invoiceform, container, false);

        db = new DatabaseHelper(getContext());
        itemsContainer = view.findViewById(R.id.itemsContainer);
        addItemButton = view.findViewById(R.id.addItemButton);
        generateOrderButton = view.findViewById(R.id.generateOrderButton);
        itemViews = new ArrayList<>();

        customerNameEditText = view.findViewById(R.id.customerNameEditText);
        invoiceDateEditText = view.findViewById(R.id.invoiceDateEditText);
        invoiceNameEditText = view.findViewById(R.id.invoiceNameEditText);

        addItemButton.setOnClickListener(v -> addItemView());
        generateOrderButton.setOnClickListener(v -> {
            if (checkAndRequestPermissions()) {
                generateOrder();
            }
        });

        addItemView(); // Add the first item view by default
        return view;
    }

    private void addItemView() {
        View itemView = getLayoutInflater().inflate(R.layout.item_row, null);
        Spinner itemSpinner = itemView.findViewById(R.id.Itemspinner);
        ImageButton deleteButton = itemView.findViewById(R.id.deleteItemButton);
        loadItems(itemSpinner);

        // Set up the delete button
        deleteButton.setOnClickListener(v -> {
            itemsContainer.removeView(itemView);
            itemViews.remove(itemView);
        });

        itemsContainer.addView(itemView);
        itemViews.add(itemView);
    }

    private void loadItems(Spinner spinner) {
        List<Item> items = db.getAllItems();
        ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private boolean checkAndRequestPermissions() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                permissionList.add(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), permissionList.toArray(new String[0]), REQUEST_CODE_PERMISSIONS);
            return false; // Permissions not yet granted, request was made.
        }
        return true; // Permissions are already granted.
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0) {
                boolean permissionsGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        permissionsGranted = false;
                        break;
                    }
                }
                if (permissionsGranted) {
                    generateOrder();
                } else {
                    Toast.makeText(getContext(), "Permissions are required to generate the PDF.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void generateOrder() {
        boolean isValid = true;
        List<Item> orderItems = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        String customerName = customerNameEditText.getText().toString().trim();
        String invoiceName = invoiceNameEditText.getText().toString().trim();
        String invoiceDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (TextUtils.isEmpty(customerName)) {
            isValid = false;
            Toast.makeText(getContext(), "Please enter customer name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(invoiceName)) {
            isValid = false;
            Toast.makeText(getContext(), "Please enter invoice name", Toast.LENGTH_SHORT).show();
            return;
        }

        for (View itemView : itemViews) {
            Spinner itemSpinner = itemView.findViewById(R.id.Itemspinner);
            EditText quantityEditText = itemView.findViewById(R.id.quantityEditText);
            Item selectedItem = (Item) itemSpinner.getSelectedItem();
            String quantityStr = quantityEditText.getText().toString().trim();

            if (selectedItem == null || TextUtils.isEmpty(quantityStr)) {
                isValid = false;
                Toast.makeText(getContext(), "Please select an item and enter quantity for all rows", Toast.LENGTH_SHORT).show();
                break;
            }

            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                isValid = false;
                Toast.makeText(getContext(), "Quantity must be greater than 0 for all items", Toast.LENGTH_SHORT).show();
                break;
            }

            int currentQuantity = selectedItem.getQuantity();
            if (quantity > currentQuantity) {
                isValid = false;
                Toast.makeText(getContext(), "Insufficient stock available for " + selectedItem.getTitle(), Toast.LENGTH_SHORT).show();
                break;
            }

            orderItems.add(selectedItem);
            quantities.add(quantity);
        }

        if (isValid) {
            for (int i = 0; i < orderItems.size(); i++) {
                Item selectedItem = orderItems.get(i);
                int quantity = quantities.get(i);

                selectedItem.setQuantity(selectedItem.getQuantity() - quantity);
                db.updateItem(selectedItem);
            }

            try {
                generatePdf(invoiceName, customerName, invoiceDate, orderItems, quantities);
                Toast.makeText(getContext(), "Order generated successfully", Toast.LENGTH_SHORT).show();

                // Launch InvoiceDisplayActivity with details
                Intent intent = new Intent(getContext(), InvoiceDisplayActivity.class);
                intent.putExtra("invoiceName", invoiceName);
                intent.putExtra("customerName", customerName);
                intent.putExtra("invoiceDate", invoiceDate);

                ArrayList<String> itemDescriptions = new ArrayList<>();
                ArrayList<Integer> itemQuantities = new ArrayList<>();

                for (int i = 0; i < orderItems.size(); i++) {
                    Item item = orderItems.get(i);
                    itemDescriptions.add(item.getTitle());
                    itemQuantities.add(quantities.get(i));
                }

                intent.putStringArrayListExtra("itemDescriptions", itemDescriptions);
                intent.putIntegerArrayListExtra("itemQuantities", itemQuantities);
                startActivity(intent);
            } catch (FileNotFoundException e) {
                Toast.makeText(getContext(), "Error generating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void generatePdf(String invoiceName, String customerName, String invoiceDate, List<Item> items, List<Integer> quantities) throws FileNotFoundException {
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
        }

        String cleanInvoiceName = invoiceName.replace(" ", "_");
        String path = directory.toString() + "/invoice_" + cleanInvoiceName + "_" + invoiceDate + ".pdf";
        PdfWriter writer = new PdfWriter(path);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Invoice Summary"));
        document.add(new Paragraph("Invoice Name: " + invoiceName));
        document.add(new Paragraph("Customer Name: " + customerName));
        document.add(new Paragraph("Invoice Date: " + invoiceDate));

        Table table = new Table(UnitValue.createPercentArray(new float[]{4, 4}));
        table.addHeaderCell("Item");
        table.addHeaderCell("Quantity");

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            int quantity = quantities.get(i);

            table.addCell(item.getTitle());
            table.addCell(String.valueOf(quantity));
        }

        document.add(table);
        document.close();
    }
}
