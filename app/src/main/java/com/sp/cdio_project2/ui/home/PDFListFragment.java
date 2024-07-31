package com.sp.cdio_project2.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sp.cdio_project2.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PDFListFragment extends Fragment {

    private RecyclerView recyclerView;
    private PDFAdapter pdfAdapter;
    private List<File> pdfFiles;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get PDF files and set up adapter
        pdfFiles = getPDFFiles();
        pdfAdapter = new PDFAdapter(pdfFiles, new PDFAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(File file) {
                openPDF(file);
            }

            @Override
            public void onDeleteClick(File file) {
                deletePDF(file);
            }
        });
        recyclerView.setAdapter(pdfAdapter);

        return view;
    }

    private List<File> getPDFFiles() {
        List<File> pdfList = new ArrayList<>();
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".pdf")) {
                    pdfList.add(file);
                }
            }
        }
        return pdfList;
    }

    private void openPDF(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(getUriForFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "No application to view PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getUriForFile(File file) {
        return FileProvider.getUriForFile(getContext(), "com.sp.cdio_project2.fileprovider", file);
    }

    private void deletePDF(File file) {
        if (file.exists() && file.delete()) {
            pdfFiles.remove(file);
            pdfAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "PDF deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to delete PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
