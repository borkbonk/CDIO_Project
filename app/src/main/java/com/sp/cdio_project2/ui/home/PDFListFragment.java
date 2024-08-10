package com.sp.cdio_project2.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sp.cdio_project2.R;

import java.util.List;

public class PDFListFragment extends Fragment {

    private RecyclerView recyclerView;
    private PDFAdapter pdfAdapter;
    private PdfViewModel pdfViewModel;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::fetchPDFFilesAndSetupAdapter);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        pdfViewModel = new ViewModelProvider(requireActivity()).get(PdfViewModel.class);

        pdfViewModel.getPdfMetadataList().observe(getViewLifecycleOwner(), pdfMetadata -> {
            if (pdfMetadata != null) {
                pdfAdapter = new PDFAdapter(pdfMetadata, new PDFAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(PdfMetadata metadata) {
                        openPDF(metadata);
                    }

                    @Override
                    public void onDeleteClick(PdfMetadata metadata) {
                        deletePDF(metadata);
                    }
                });
                recyclerView.setAdapter(pdfAdapter);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pdfViewModel.getPdfMetadataList().getValue() == null || pdfViewModel.getPdfMetadataList().getValue().isEmpty()) {
            fetchPDFFilesAndSetupAdapter();
        }
    }

    private void fetchPDFFilesAndSetupAdapter() {
        swipeRefreshLayout.setRefreshing(true);
        firestore.collection("pdf_metadata")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<PdfMetadata> metadataList = queryDocumentSnapshots.toObjects(PdfMetadata.class);
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                        metadataList.get(i).setDocumentId(queryDocumentSnapshots.getDocuments().get(i).getId());
                    }
                    pdfViewModel.setPdfMetadataList(metadataList);
                    swipeRefreshLayout.setRefreshing(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching PDF metadata", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
    }

    private void openPDF(PdfMetadata metadata) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(metadata.getUrl()), "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "No application to view PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void deletePDF(PdfMetadata metadata) {
        if (metadata.getUrl() == null || metadata.getUrl().isEmpty()) {
            Toast.makeText(getContext(), "PDF URL is missing. Unable to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        String documentId = metadata.getDocumentId();
        if (documentId != null && !documentId.isEmpty()) {
            StorageReference storageReference = storage.getReferenceFromUrl(metadata.getUrl());

            storageReference.delete().addOnSuccessListener(aVoid -> {
                firestore.collection("pdf_metadata").document(documentId)
                        .delete()
                        .addOnSuccessListener(unused -> {
                            List<PdfMetadata> currentList = pdfViewModel.getPdfMetadataList().getValue();
                            if (currentList != null) {
                                currentList.remove(metadata);
                                pdfViewModel.setPdfMetadataList(currentList);
                                Toast.makeText(getContext(), "PDF deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete PDF metadata from Firestore", Toast.LENGTH_SHORT).show());
            }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete PDF file from Firebase Storage", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "Invalid Document ID", Toast.LENGTH_SHORT).show();
        }
    }
}
