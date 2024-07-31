package com.sp.cdio_project2.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sp.cdio_project2.R;

import java.io.File;
import java.util.List;

public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.PDFViewHolder> {

    private List<File> pdfFiles;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(File file);
        void onDeleteClick(File file);
    }

    public PDFAdapter(List<File> pdfFiles, OnItemClickListener onItemClickListener) {
        this.pdfFiles = pdfFiles;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public PDFViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pdf, parent, false);
        return new PDFViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PDFViewHolder holder, int position) {
        File pdfFile = pdfFiles.get(position);
        holder.bind(pdfFile, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return pdfFiles.size();
    }

    static class PDFViewHolder extends RecyclerView.ViewHolder {

        private TextView pdfTitle;
        private TextView invoiceDateLabel;
        private TextView deleteButton;

        public PDFViewHolder(@NonNull View itemView) {
            super(itemView);
            pdfTitle = itemView.findViewById(R.id.pdfTitle);
            invoiceDateLabel = itemView.findViewById(R.id.invoiceDate);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(final File pdfFile, final OnItemClickListener listener) {
            String fileName = pdfFile.getName();
            String[] parts = fileName.split("_");  // Split by underscore
            if (parts.length >= 3) {
                String invoiceName = parts[1].replace("_", " ");  // The name part between the underscores
                String invoiceDate = parts[2].replace(".pdf", ""); // The date part after the last underscore
                pdfTitle.setText(invoiceName);
                invoiceDateLabel.setText(invoiceDate);
            } else {
                pdfTitle.setText(fileName);
                invoiceDateLabel.setText("Unknown Date");
            }

            itemView.setOnClickListener(v -> listener.onItemClick(pdfFile));
            deleteButton.setOnClickListener(v -> listener.onDeleteClick(pdfFile));
        }
    }
}
