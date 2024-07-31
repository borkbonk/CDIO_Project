package com.sp.cdio_project2.ui.home;

public class PdfMetadata {
    private String fileName;
    private String url;
    private String companyName;
    private String companyAddress;
    private String invoiceName;
    private String invoiceDate;

    // Default constructor required for calls to DataSnapshot.getValue(PdfMetadata.class)
    public PdfMetadata() { }

    public PdfMetadata(String fileName, String url, String companyName, String companyAddress, String invoiceName, String invoiceDate) {
        this.fileName = fileName;
        this.url = url;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.invoiceName = invoiceName;
        this.invoiceDate = invoiceDate;
    }

    // Getter and Setter methods

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getInvoiceName() {
        return invoiceName;
    }

    public void setInvoiceName(String invoiceName) {
        this.invoiceName = invoiceName;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
}
