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
          //      Toast.makeText(getActivity(),"下载申请文件文本\n" + "申请号：" + case_number, Toast.LENGTH_LONG).show();
                String patentPath = Environment.getExternalStorageDirectory().getPath()+"/download/";
             //   Toast.makeText(getActivity(),"目标文件：\n" + patentPath + case_number + ".0.txt", Toast.LENGTH_LONG).show();
                if(!writeTxtToFile(strCaseNum,patentPath,strCaseNum + ".0.txt"))
                    return;
                Toast.makeText(getActivity(),"写文件成功：\n" + strCaseNum + ".0.txt", Toast.LENGTH_SHORT).show();
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    File file = new File(patentPath,strCaseNum + ".0.txt");
                 //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                //      Toast.makeText(getActivity(),"下载申请文件文本\n" + "申请号：" + case_number, Toast.LENGTH_LONG).show();
                String patentPath = Environment.getExternalStorageDirectory().getPath()+"/download/";
                //   Toast.makeText(getActivity(),"目标文件：\n" + patentPath + case_number + ".0.txt", Toast.LENGTH_LONG).show();
                if(!writeTxtToFile(strCaseNum,patentPath,strCaseNum + ".0.pdf"))
                    return;
                Toast.makeText(getActivity(),"写文件成功：\n" + strCaseNum + ".0.pdf", Toast.LENGTH_SHORT).show();
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    File file = new File(patentPath,strCaseNum + ".0.pdf");
                    //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                //      Toast.makeText(getActivity(),"下载申请文件文本\n" + "申请号：" + case_number, Toast.LENGTH_LONG).show();
                String patentTxTPath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,12)+".txt";
                File txtFile =new File(patentTxTPath);
                if(!txtFile.exists()) {
                    Toast.makeText(getActivity(),"目标文件：\n" + patentTxTPath + "不存在", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getActivity(),"找到目标文件：\n" + patentTxTPath , Toast.LENGTH_LONG).show();
                try {
                    String pTxt = readFileData(patentTxTPath);
              //      Toast.makeText(getActivity(), pTxt.substring(0, 100), Toast.LENGTH_LONG).show();
      //              if (1 > 0)
      //                  return;
                    String[] ptn = pTxt.split("\n");
            //        Toast.makeText(getActivity(), ptn[0], Toast.LENGTH_LONG).show();
            //        Toast.makeText(getActivity(), ptn[1], Toast.LENGTH_LONG).show();
            //        Toast.makeText(getActivity(), ptn[2], Toast.LENGTH_LONG).show();
                    if (ptn.length > 3) {
                        String strIC = ptn[1];
                        String strPN = ptn[2];
                        String ic = "";
                        if(strIC.indexOf("/")>0) {
                            ic = strIC.substring(strIC.indexOf("-") + 1, strIC.indexOf("/"));
                        }else
                            strPN = ptn[1];
                        String pn = strPN.substring(strPN.lastIndexOf(" ") + 1);
                        String pn1 = pn.substring(0, 4) + "-" + pn.substring(4,6) + "-" + pn.substring(6, 8);
                        pn1 = pn1.replaceAll("-0","-");
                        apdEText.setText(pn1);
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
    // 将字符串写入到文本文件中
    private boolean writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        if(makeFilePath(filePath, fileName)==null){
             return false;
        }
        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if(file.exists())
                file.delete();
            file.createNewFile();
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(0);
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Toast.makeText(getActivity(),"Error on write File:\n" + e, Toast.LENGTH_LONG).show();
            return false;
         //   Log.e("TestFile", "Error on write File:" + e);
        }
        return true;
    }

//生成文件

    private File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"Error on makeFilePath File:\n" + filePath + fileName + "\n" +e, Toast.LENGTH_LONG).show();
            file = null;
        }
        return file;
    }

//生成文件夹

    private static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            //Log.i("error:", e + "");
        }
    }
    //打开指定文件，读取其数据，返回字符串对象
    public String readFileData(String fileName){
        String result="";
        try{
            FileReader freader = new FileReader(fileName);
            //获取文件长度
            BufferedReader br =new BufferedReader(freader);

/*4.可以调用字符缓冲流br的readLine()方法度一行输入文本*/
            String line =null;
            while((line =br.readLine())!=null){
                result = result + line +"\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"读文件"+fileName+"失败\n"+e.toString(), Toast.LENGTH_LONG).show();
        }
        return  result;
    }
}
