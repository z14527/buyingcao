package com.gyg.buyingcao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aview);
        TextView textView = (TextView) this.findViewById(R.id.textview);

        float zoomScale = 0.5f;// 缩放比例
        new LianxiText(textView, zoomScale);
    }
}
