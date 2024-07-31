package com.sp.cdio_project2.form;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sp.cdio_project2.Database.FirebaseHelper;
import com.sp.cdio_project2.R;
import com.sp.cdio_project2.ui.dashboard.Item;
import com.sp.cdio_project2.ui.home.PdfMetadata;
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
    private FirebaseHelper firebaseHelper;
    private LinearLayout itemsContainer;
    private Button addItemButton;
    private Button generateOrderButton;
    private List<View> itemViews;

    private EditText companyNameEditText;
    private EditText companyAddressEditText;
    private EditText customerNameEditText;
    private EditText invoiceDateEditText;
    private EditText invoiceNameEditText;

    private static final String TAG = "Invoiceform";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_invoiceform, container, false);

        firebaseHelper = new FirebaseHelper();
        itemsContainer = view.findViewById(R.id.itemsContainer);
        addItemButton = view.findViewById(R.id.addItemButton);
        generateOrderButton = view.findViewById(R.id.generateOrderButton);
        itemViews = new ArrayList<>();

        companyNameEditText = view.findViewById(R.id.companyNameEditText);
        companyAddressEditText = view.findViewById(R.id.companyAddressEditText);
        customerNameEditText = view.findViewById(R.id.customerNameEditText);
        invoiceDateEditText = view.findViewById(R.id.invoiceDateEditText);
        invoiceNameEditText = view.findViewById(R.id.invoiceNameEditText);

        addItemButton.setOnClickListener(v -> addItemView());
        generateOrderButton.setOnClickListener(v -> generateOrder());

        addItemView(); // Add the first item view by default
        return view;
    }

    private void addItemView() {
        View itemView = getLayoutInflater().inflate(R.layout.item_row, null);
        Spinner itemSpinner = itemView.findViewById(R.id.Itemspinner);
        ImageButton deleteButton = itemView.findViewById(R.id.deleteItemButton);
        loadItems(itemSpinner);

        deleteButton.setOnClickListener(v -> {
            itemsContainer.removeView(itemView);
            itemViews.remove(itemView);
        });

        itemsContainer.addView(itemView);
        itemViews.add(itemView);
    }

    private void loadItems(Spinner spinner) {
        MutableLiveData<List<Item>> itemsLiveData = new MutableLiveData<>();
        firebaseHelper.getAllItems(itemsLiveData, e -> Log.e(TAG, "Error getting items", e));

        itemsLiveData.observe(getViewLifecycleOwner(), items -> {
            ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            Log.d(TAG, "Spinner loaded with items");
        });
    }

    private void generateOrder() {
        boolean isValid = true;
        List<Item> orderItems = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        String companyName = companyNameEditText.getText().toString().trim();
        String companyAddress = companyAddressEditText.getText().toString().trim();
        String customerName = customerNameEditText.getText().toString().trim();
        String invoiceName = invoiceNameEditText.getText().toString().trim();
        String invoiceDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (TextUtils.isEmpty(companyName)) {
            isValid = false;
            Toast.makeText(getContext(), "Please enter company name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(companyAddress)) {
            isValid = false;
            Toast.makeText(getContext(), "Please enter company address", Toast.LENGTH_SHORT).show();
            return;
        }
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
                updateItemInFirebase(selectedItem);
            }

            try {
                String pdfPath = generatePdf(companyName, companyAddress, invoiceName, customerName, invoiceDate, orderItems, quantities);
                Log.d(TAG, "Generated PDF Path: " + pdfPath);
                uploadPdfToStorage(pdfPath, companyName, companyAddress, invoiceName, invoiceDate);
                Toast.makeText(getContext(), "Order generated successfully", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                Toast.makeText(getContext(), "Error generating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to generate PDF", e);
            }
        }
    }

    private void updateItemInFirebase(Item item) {
        firebaseHelper.updateItem(item.getId(), item, unused -> {
            Log.d(TAG, "Item updated in Firebase: " + item.getTitle());
        }, e -> Log.e(TAG, "Error updating item", e));
    }

    private String generatePdf(String companyName, String companyAddress, String invoiceName, String customerName, String invoiceDate, List<Item> items, List<Integer> quantities) throws FileNotFoundException {
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
            Log.d(TAG, "Directory created: " + directory.getPath());
        }

        String cleanInvoiceName = invoiceName.replace(" ", "_");
        String path = directory.toString() + "/invoice_" + cleanInvoiceName + "_" + invoiceDate + ".pdf";
        PdfWriter writer = new PdfWriter(path);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Invoice Summary"));
        document.add(new Paragraph("Company Name: " + companyName));
        document.add(new Paragraph("Company Address: " + companyAddress));
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
        Log.d(TAG, "PDF generated: " + path);

        return path;
    }

    private void uploadPdfToStorage(String pdfPath, String companyName, String companyAddress, String invoiceName, String invoiceDate) {
        File pdfFile = new File(pdfPath);
        if (!pdfFile.exists()) {
            Log.e(TAG, "PDF file does not exist at path: " + pdfPath);
            return;
        }

        Uri fileUri = Uri.fromFile(pdfFile);
        Log.d(TAG, "File URI: " + fileUri.toString());

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference pdfRef = storageRef.child("invoices/" + fileUri.getLastPathSegment());

        pdfRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    pdfRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Log.d(TAG, "PDF uploaded. Download URL: " + uri.toString());
                        savePdfMetadataToFirestore(uri.toString(), companyName, companyAddress, invoiceName, invoiceDate);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error uploading PDF: ", e);
                    Toast.makeText(getContext(), "Failed to upload PDF", Toast.LENGTH_SHORT).show();
                });
    }

    private void savePdfMetadataToFirestore(String downloadUrl, String companyName, String companyAddress, String invoiceName, String invoiceDate) {
        String pdfFileName = "invoice_" + invoiceName.replace(" ", "<em>") + "</em>" + invoiceDate + ".pdf";
        PdfMetadata metadata = new PdfMetadata(pdfFileName, downloadUrl, companyName, companyAddress, invoiceName, invoiceDate);

        firebaseHelper.addPdfMetadata(metadata, documentReference -> {
            Log.d(TAG, "PDF metadata added with ID: " + documentReference.getId());
            Toast.makeText(getContext(), "PDF metadata added successfully", Toast.LENGTH_SHORT).show();
        }, e -> {
            Log.e(TAG, "Error adding PDF metadata: ", e);
            Toast.makeText(getContext(), "Failed to add PDF metadata", Toast.LENGTH_SHORT).show();
        });
    }
}
