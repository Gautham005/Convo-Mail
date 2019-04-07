package com.example.convomail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PDFViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        Intent i = getIntent();
        String fileName = i.getStringExtra("fileName");
        File file = new File(fileName);
        PDFView pdfView = findViewById(R.id.pdfView);
        Uri apkURI = FileProvider.getUriForFile(
                this,
                this.getApplicationContext()
                        .getPackageName() + ".provider", file);
        pdfView.fromUri(apkURI).load();
    }
}
