package com.sp.cdio_project2.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class PdfViewModel extends ViewModel {
    private MutableLiveData<List<PdfMetadata>> pdfMetadataList = new MutableLiveData<>();

    public void setPdfMetadataList(List<PdfMetadata> metadataList) {
        pdfMetadataList.setValue(metadataList);
    }

    public LiveData<List<PdfMetadata>> getPdfMetadataList() {
        return pdfMetadataList;
    }
}
