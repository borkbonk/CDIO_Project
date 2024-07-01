package com.sp.cdio_project2.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.sp.cdio_project2.R;

public class scanqr extends AppCompatActivity {

    Button scan_btn;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_qr);

        scan_btn = findViewById(R.id.scanQr);
        textView = findViewById(R.id.qrResult);

        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(scanqr.this);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("Scan qr code");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.initiateScan();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentresult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentresult != null) {
            String contents = intentresult.getContents();
            if (contents != null) {
                textView.setText(intentresult.getContents());


            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}
