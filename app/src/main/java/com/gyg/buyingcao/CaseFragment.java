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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Math.min;


public class CaseFragment extends Fragment {
    private TextView textView;
    private Button btnImport,btnOK,btnDTxt,btnDPdf,btnClear,btnExec,btnGetFile,btnGetSx,btnDZhulu,btnDSCAJ,btnDPCT;
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
                 String patentTxTPath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+".txt";
                File txtFile =new File(patentTxTPath);
                if(!txtFile.exists()) {
                    Toast.makeText(getActivity(),"目标文件：\n" + patentTxTPath + "不存在", Toast.LENGTH_LONG).show();
                    return;
                }
          //      Toast.makeText(getActivity(),"找到目标文件：\n" + patentTxTPath , Toast.LENGTH_LONG).show();
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
                        apdEText.setText(ap);
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
                strCaseNum = numEText.getText().toString().replaceAll(" |[^0-9]","");
                if(strCaseNum.length()<3){
                    Toast.makeText(getActivity(),"申请号格式不对", Toast.LENGTH_LONG).show();
                    return;
                }
                editor = pref.edit();
                strCaseApd = apdEText.getText().toString();
                strCaseClass = classEText.getText().toString();
                editor.putString("CaseNum",strCaseNum);
                editor.putString("CaseApd",strCaseApd);
                editor.putString("CaseClass",strCaseClass);
                editor.commit();
                numEText.setText(strCaseNum);
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
        btnExec=(Button)getActivity().findViewById(R.id.case_exec);
        btnExec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+".c.txt";
                Intent intent = new Intent(getContext(),RichEditActivity.class);
                intent.putExtra("fname",txtFilePath);
                intent.putExtra("type","4");
                intent.putExtra("cmd","");
                startActivity(intent);
            }
        });
        btnDZhulu=(Button)getActivity().findViewById(R.id.case_get_zhulu);
        btnDZhulu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+".c.txt";
                Intent intent = new Intent(getContext(),RichEditActivity.class);
                intent.putExtra("fname",txtFilePath);
                intent.putExtra("type","6");
                String strCmd = "echo \"" + strCaseNum +
                        "\" > d:\\temp\\sipoe.txt \n" +
                        "d:\\workspace\\sipoe0605\\getSipoeZ.bat d:\\temp\\sipoe.txt";
                intent.putExtra("cmd",strCmd);
                startActivity(intent);
            }
        });
        btnDSCAJ=(Button)getActivity().findViewById(R.id.case_get_exam);
        btnDSCAJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+".c.txt";
                Intent intent = new Intent(getContext(),RichEditActivity.class);
                intent.putExtra("fname",txtFilePath);
                intent.putExtra("type","6");
                String strCmd = "echo \"" + strCaseNum +
                        "\" > d:\\temp\\sipoe.txt \n" +
                        "d:\\workspace\\sipoe0605\\getSipoe.bat d:\\temp\\sipoe.txt";
                intent.putExtra("cmd",strCmd);
                startActivity(intent);
            }
        });
        btnDPCT=(Button)getActivity().findViewById(R.id.case_get_pct);
        btnDPCT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+".c.txt";
                Intent intent = new Intent(getContext(),RichEditActivity.class);
                intent.putExtra("fname",txtFilePath);
                intent.putExtra("type","6");
                String PCTN = strCaseNum;
                if(strCaseNum.length()>=10)
                    PCTN = "PCT/CN"+strCaseNum.substring(0,4)+"/"+strCaseNum.substring(4,10);
                String strCmd = "echo \"" + PCTN +
                        "\" > d:\\temp\\sipoe.txt \n" +
                        "d:\\workspace\\sipoe0605\\getSipoePCT.bat d:\\temp\\sipoe.txt";
                intent.putExtra("cmd",strCmd);
                startActivity(intent);
            }
        });
        btnGetFile=(Button)getActivity().findViewById(R.id.case_get_file);
        btnGetFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+".f.txt";
                Intent intent = new Intent(getContext(),RichEditActivity.class);
                intent.putExtra("fname",txtFilePath);
                intent.putExtra("type","4");
                startActivity(intent);
            }
        });
        btnGetSx=(Button)getActivity().findViewById(R.id.case_get_sx);
        btnGetSx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strCaseNum = numEText.getText().toString();
                String patentPath = Environment.getExternalStorageDirectory().getPath()+"/download/";
                if(!(new MyUtil(getActivity()).writeTxtToFile(strCaseNum,patentPath,"p"+strCaseNum + ".s.txt")))
                    return;
                Toast.makeText(getActivity(),"写文件成功：\n" + strCaseNum + ".s.txt", Toast.LENGTH_SHORT).show();
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    File file = new File(patentPath,"p"+strCaseNum + ".s.txt");
                    Uri uri = FileProvider7.getUriForFile(getContext(),file);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);  //传输图片或者文件 采用流的方式
                    intent.setType("*/*");   //分享文件
                    startActivity(Intent.createChooser(intent, "分享"));
                }catch (Exception e) {
                    Toast.makeText(getActivity(),"Error on action send:\n" + e, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
