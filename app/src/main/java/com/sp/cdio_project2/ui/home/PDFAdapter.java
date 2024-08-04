package com.sp.cdio_project2.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sp.cdio_project2.R;

import java.util.List;

public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.PDFViewHolder> {

    private List<PdfMetadata> pdfMetadataList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(PdfMetadata metadata);
        void onDeleteClick(PdfMetadata metadata);
    }

    public PDFAdapter(List<PdfMetadata> pdfMetadataList, OnItemClickListener onItemClickListener) {
        this.pdfMetadataList = pdfMetadataList;
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
        PdfMetadata metadata = pdfMetadataList.get(position);
        holder.bind(metadata, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return pdfMetadataList.size();
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

        public void bind(final PdfMetadata metadata, final OnItemClickListener listener) {
            pdfTitle.setText(metadata.getInvoiceName());
            invoiceDateLabel.setText(metadata.getInvoiceDate());
            itemView.setOnClickListener(v -> listener.onItemClick(metadata));
            deleteButton.setOnClickListener(v -> listener.onDeleteClick(metadata));
        }
    }
}
