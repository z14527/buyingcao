package com.gyg.buyingcao;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PDFViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        PDFView pdfView = (PDFView)findViewById(R.id.pdfView);
        String pdfFilePath = getIntent().getStringExtra("fname");
        File f1 = new File(pdfFilePath);
        pdfView.fromFile(f1);
    }

}
