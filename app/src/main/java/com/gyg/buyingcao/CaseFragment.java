package com.gyg.buyingcao;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.base.fileprovider.FileProvider7;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

import static android.content.Context.MODE_PRIVATE;


public class CaseFragment extends Fragment {
    private TextView textView;
    private Button btnImport,btnOK,btnDTxt,btnDPdf,btnClear;
    private EditText numEText,apdEText,classEText;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String strCaseNum="",strCaseApd="",strCaseClass="";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_case,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
      //  textView=(TextView)getActivity().findViewById(R.id.case_textView1);
        strCaseNum = pref.getString("CaseNum","");
        strCaseApd = pref.getString("CaseApd","");
        strCaseClass = pref.getString("CaseClass","");
        numEText=(EditText)getActivity().findViewById(R.id.case_number);
        apdEText=(EditText)getActivity().findViewById(R.id.case_date);
        classEText=(EditText)getActivity().findViewById(R.id.case_class);
        numEText.setText(strCaseNum);
        apdEText.setText(strCaseApd);
        classEText.setText(strCaseClass);
        btnDTxt=(Button)getActivity().findViewById(R.id.case_txt_down);
        btnDTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strCaseNum = numEText.getText().toString();
                String patentPath = Environment.getExternalStorageDirectory().getPath()+"/download/";
                if(!(new MyUtil(getActivity()).writeTxtToFile(strCaseNum,patentPath,"t"+strCaseNum + ".0.txt")))
                    return;
                Toast.makeText(getActivity(),"写文件成功：\n" + strCaseNum + ".0.txt", Toast.LENGTH_SHORT).show();
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    File file = new File(patentPath,"t"+strCaseNum + ".0.txt");
                    Uri uri = FileProvider7.getUriForFile(getContext(),file);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);  //传输图片或者文件 采用流的方式
                    intent.setType("*/*");   //分享文件
                    startActivity(Intent.createChooser(intent, "分享"));
                }catch (Exception e) {
                    Toast.makeText(getActivity(),"Error on action send:\n" + e, Toast.LENGTH_LONG).show();
                }
            }
        });
        btnDPdf=(Button)getActivity().findViewById(R.id.case_pdf_down);
        btnDPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strCaseNum = numEText.getText().toString();
                String patentPath = Environment.getExternalStorageDirectory().getPath()+"/download/";
                if(!(new MyUtil(getActivity()).writeTxtToFile(strCaseNum,patentPath,"p"+strCaseNum + ".0.pdf")))
                    return;
                Toast.makeText(getActivity(),"写文件成功：\n" + strCaseNum + ".0.pdf", Toast.LENGTH_SHORT).show();
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    File file = new File(patentPath,"p"+strCaseNum + ".0.pdf");
                    Uri uri = FileProvider7.getUriForFile(getContext(),file);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);  //传输图片或者文件 采用流的方式
                    intent.setType("*/*");   //分享文件
                    startActivity(Intent.createChooser(intent, "分享"));
                }catch (Exception e) {
                    Toast.makeText(getActivity(),"Error on action send:\n" + e, Toast.LENGTH_LONG).show();
                }
            }
        });

        btnImport=(Button)getActivity().findViewById(R.id.case_import);
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strCaseNum = numEText.getText().toString();
                 String patentTxTPath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,12)+".txt";
                File txtFile =new File(patentTxTPath);
                if(!txtFile.exists()) {
                    Toast.makeText(getActivity(),"目标文件：\n" + patentTxTPath + "不存在", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getActivity(),"找到目标文件：\n" + patentTxTPath , Toast.LENGTH_LONG).show();
                try {
                    String pTxt = new MyUtil(getActivity()).readFileData(patentTxTPath);
                    String[] ptn = pTxt.split("\n");
                    if (ptn.length > 3) {
                        String strIC = ptn[1];
                        String strAP = ptn[0];
                        String ic = "";
                        if(strIC.indexOf("/")>0) {
                            ic = strIC.substring(strIC.indexOf("-") + 1, strIC.indexOf("/"));
                        }
                        String ap = strAP.substring(strAP.lastIndexOf(" ") + 1);
                        String ap1 = ap.substring(0, 4) + "-" + ap.substring(4,6) + "-" + ap.substring(6, 8);
                        ap1 = ap1.replaceAll("-0","-");
                        apdEText.setText(ap1);
                        classEText.setText(ic);
                    }
                }catch(Exception e1){
                    Toast.makeText(getActivity(),e1.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        btnOK=(Button)getActivity().findViewById(R.id.case_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = pref.edit();
                strCaseNum = numEText.getText().toString();
                strCaseApd = apdEText.getText().toString();
                strCaseClass = classEText.getText().toString();
                editor.putString("CaseNum",strCaseNum);
                editor.putString("CaseApd",strCaseApd);
                editor.putString("CaseClass",strCaseClass);
                editor.commit();
            }
        });
        btnClear=(Button)getActivity().findViewById(R.id.case_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numEText.setText("");
                apdEText.setText("");
                classEText.setText("");
             }
        });
    }
}
