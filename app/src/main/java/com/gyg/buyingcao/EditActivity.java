package com.gyg.buyingcao;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

public class EditActivity extends AppCompatActivity {

    EditText editText = null;
    TextView tvOK = null,tvQuit = null;
    String txtFilePath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editText = (EditText) this.findViewById(R.id.et_text);
      //  editText.setSelected(true);
        tvOK = (TextView)findViewById(R.id.tv_save);
        tvQuit = (TextView)findViewById(R.id.tv_quit);
        File txtFile = null;
        try {
            txtFilePath = getIntent().getStringExtra("fname");
            txtFile = new File(txtFilePath);
        }catch (Exception e1){
            Toast.makeText(this,e1.toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if(!txtFile.exists()) {
            Toast.makeText(this,"目标文件：\n" + txtFilePath + "不存在", Toast.LENGTH_LONG).show();
            return;
        }else {
            String strTxt = "";
            try {
                strTxt = new MyUtil(getApplication()).readExternal(txtFilePath);
                editText.setText(strTxt);
                editText.setMovementMethod(ScrollingMovementMethod.getInstance());
                editText.setVerticalScrollBarEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try{
                    String keys = editText.getText().toString();
                    if(keys.indexOf("---")>0)
                        keys = keys.split("---")[0];
                    new MyUtil(getApplication()).writeTxtToFile(keys,txtFilePath);
                    Toast.makeText(getApplication(), "已经保存", Toast.LENGTH_LONG).show();
                    finish();
                }catch (Exception e) {
                    Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }

        });
        tvQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                    finish();
            }
        });
    }

}
