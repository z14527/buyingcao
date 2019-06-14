package com.gyg.buyingcao;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.min;

public class ViewFragment extends Fragment {
   // private TextView result;
    private Button btnCaseTxt,btnCasePdf,btnCaseKey,btnCaseCNKeyExpand,btnCaseENKeyExpand,btnCaseCNSearch,btnCaseENSearch,btnCaseSearchRunHistory,btnCaseResultView,btnCaseSearchFileSelectView,btnSxFileView,btnCaseZhulu;

    private File file;
    private String path = "";
    private String info = "";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String strCaseNum="",strCaseApd="",strCaseClass="";
    private String key = ""; //关键字
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_view,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
     //   result=(TextView)getActivity().findViewById(R.id.view_textView1);
      //  result.setMovementMethod(ScrollingMovementMethod.getInstance());
    //    button=(Button)getActivity().findViewById(R.id.view_button1);
        btnCaseTxt=(Button)getActivity().findViewById(R.id.case_txt_view);
        btnCaseKey=(Button)getActivity().findViewById(R.id.case_key_view);
        strCaseNum = pref.getString("CaseNum","");
     //   EditText key_editText = getActivity().findViewById(R.id.file_key);
      //  key_editText.setText(strCaseNum);
        path = Environment.getExternalStorageDirectory().getPath()+"/download/";
        btnCaseTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sdPath = Environment.getExternalStorageDirectory().getPath() + "/download/";
                String[] filePaths = {sdPath + "CN" + strCaseNum.substring(0, min(strCaseNum.length(), 12)) + ".txt", sdPath + strCaseNum.substring(0, min(strCaseNum.length(), 12)) + ".txt", sdPath + "CN" + strCaseNum + ".txt", sdPath + strCaseNum + ".txt", sdPath + "PCT-CN" + strCaseNum.substring(0, 4) + "-" + strCaseNum.substring(4, 10) + ".txt"};
                for (int i = 0; i < filePaths.length; i++) {
                    File f1 = new File(filePaths[i]);
                    if (f1.exists()) {
                        Intent intent = new Intent(getContext(), RichEditActivity.class);
                        intent.putExtra("fname", filePaths[i]);
                        intent.putExtra("type", "2");
                        startActivity(intent);
                    }
                }
            }
        });
        btnCasePdf=(Button)getActivity().findViewById(R.id.case_pdf_view);
        btnCasePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sdPath = Environment.getExternalStorageDirectory().getPath()+"/download/";
                String[] filePaths = {sdPath+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+".pdf",sdPath+strCaseNum.substring(0,min(strCaseNum.length(),12))+".pdf",sdPath+"CN"+strCaseNum+".pdf",sdPath+strCaseNum+".pdf",sdPath+"PCT-CN"+strCaseNum.substring(0,4)+"-"+strCaseNum.substring(4,10)+".pdf"};
                for(int i=0;i<filePaths.length;i++){
                    File f1 = new File(filePaths[i]);
                    if(f1.exists()) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            FileProvider7.setIntentDataAndType(getContext(), intent, "application/pdf", f1, true);
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Error on action send:\n" + e, Toast.LENGTH_LONG).show();
                        }
                        break;
                    }
                }
            }
        });
        btnCaseZhulu=(Button)getActivity().findViewById(R.id.case_zhulu_view);
        btnCaseZhulu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+"-著录项目.txt";
                if(strCaseNum.length()<12 && strCaseNum.length()>=10)
                    txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"PCT-CN"+strCaseNum.substring(0,4)+"-"+strCaseNum.substring(4,10)+"-著录项目.txt";
                Intent intent = new Intent(getContext(),RichEditActivity.class);
                intent.putExtra("fname",txtFilePath);
                intent.putExtra("type","2");
                startActivity(intent);
            }
        });
        btnCaseKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+".2.txt";
                if(strCaseNum.length()<12 && strCaseNum.length()>=10)
                    txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"PCT-CN"+strCaseNum.substring(0,4)+"-"+strCaseNum.substring(4,10)+".2.txt";
                Intent intent = new Intent(getContext(),RichEditActivity.class);
                intent.putExtra("fname",txtFilePath);
                intent.putExtra("type","2");
                startActivity(intent);
            }
        });
        btnCaseCNKeyExpand=(Button)getActivity().findViewById(R.id.case_cn_key_expand_view);
        btnCaseCNKeyExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+".3.txt";
                if(strCaseNum.length()<12 && strCaseNum.length()>=10)
                    txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"PCT-CN"+strCaseNum.substring(0,4)+"-"+strCaseNum.substring(4,10)+".3.txt";
                Intent intent = new Intent(getContext(),RichEditActivity.class);
                intent.putExtra("fname",txtFilePath);
                intent.putExtra("type","2");
                startActivity(intent);
            }
        });
        btnCaseENKeyExpand=(Button)getActivity().findViewById(R.id.case_en_key_expand_view);
        btnCaseENKeyExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+".3.e.txt";
                if(strCaseNum.length()<12 && strCaseNum.length()>=10)
                    txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"PCT-CN"+strCaseNum.substring(0,4)+"-"+strCaseNum.substring(4,10)+".3.e.txt";
                Intent intent = new Intent(getContext(),RichEditActivity.class);
                intent.putExtra("fname",txtFilePath);
                intent.putExtra("type","2");
                startActivity(intent);
            }
        });
        btnCaseCNSearch=(Button)getActivity().findViewById(R.id.case_cn_search_view);
        btnCaseCNSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+".4.txt";
                if(strCaseNum.length()<12 && strCaseNum.length()>=10)
                    txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"PCT-CN"+strCaseNum.substring(0,4)+"-"+strCaseNum.substring(4,10)+".4.txt";
                Intent intent = new Intent(getContext(),RichEditActivity.class);
                intent.putExtra("fname",txtFilePath);
                intent.putExtra("type","2");
                startActivity(intent);
            }
         });
        btnCaseENSearch=(Button)getActivity().findViewById(R.id.case_en_search_view);
        btnCaseENSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+".4.e.txt";
                if(strCaseNum.length()<12 && strCaseNum.length()>=10)
                    txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"PCT-CN"+strCaseNum.substring(0,4)+"-"+strCaseNum.substring(4,10)+".4.e.txt";
                Intent intent = new Intent(getContext(),RichEditActivity.class);
                intent.putExtra("fname",txtFilePath);
                intent.putExtra("type","2");
                startActivity(intent);
            }
        });
        btnCaseSearchRunHistory=(Button)getActivity().findViewById(R.id.case_search_run_history_view);
        btnCaseSearchRunHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+".log";
                if(strCaseNum.length()<12 && strCaseNum.length()>=10)
                    txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"PCT-CN"+strCaseNum.substring(0,4)+"-"+strCaseNum.substring(4,10)+".log";
                Intent intent = new Intent(getContext(),RichEditActivity.class);
                intent.putExtra("fname",txtFilePath);
                intent.putExtra("type","2");
                startActivity(intent);
            }
        });
        btnCaseResultView=(Button)getActivity().findViewById(R.id.case_search_result_view);
        btnCaseResultView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+".log";
                if(strCaseNum.length()<12 && strCaseNum.length()>=10)
                    txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"PCT-CN"+strCaseNum.substring(0,4)+"-"+strCaseNum.substring(4,10)+".log";
                Intent intent = new Intent(getContext(),RichEditActivity.class);
                intent.putExtra("fname",txtFilePath);
                intent.putExtra("type","3");
                startActivity(intent);
            //    Toast.makeText(getActivity(),"概览", Toast.LENGTH_SHORT).show();
            }
        });
        btnCaseSearchFileSelectView=(Button)getActivity().findViewById(R.id.case_search_files_select_view);
        btnCaseSearchFileSelectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,12)+".log";
                Intent intent = new Intent(getContext(),FileSelectActivity.class);
                //intent.putExtra("fname",txtFilePath);
                //intent.putExtra("type","3");
                startActivity(intent);
                //    Toast.makeText(getActivity(),"概览", Toast.LENGTH_SHORT).show();
            }
        });
        btnSxFileView=(Button)getActivity().findViewById(R.id.case_sx_view);
        btnSxFileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+".s.txt";
                if(strCaseNum.length()<12 && strCaseNum.length()>=10)
                    txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"PCT-CN"+strCaseNum.substring(0,4)+"-"+strCaseNum.substring(4,10)+".s.txt";
                String txtFilePath1 = Environment.getExternalStorageDirectory().getPath()+"/download/";
                List<File> fileList = new pf().listFileSortByModifyTime(txtFilePath1);
                for(File file1: fileList){
                    if(file1.getName().indexOf(".s.txt")>=0 && file1.length()>0 && file1.getName().indexOf("p")<0)
                        txtFilePath = file1.getAbsolutePath();
                }
                Intent intent = new Intent(getContext(),RichEditActivity.class);
                intent.putExtra("fname",txtFilePath);
                intent.putExtra("type","5");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        strCaseNum = pref.getString("CaseNum","");
      //  EditText key_editText = getActivity().findViewById(R.id.file_key);
     //  key_editText.setText(strCaseNum);
    }
    private void search(File fileold)
    {
        File[] files=fileold.listFiles();
        if(files.length>0)
        {
            for(int j=0;j<files.length;j++)
            {
                if(!files[j].isDirectory()) {
                 //   Pattern p = Pattern.compile(key);
                 //   if (p.matcher(files[j].getName()).matches()) {
                    if(files[j].getName().contains(key)){
                        String fname = files[j].getAbsolutePath();
                        //result.append("\n"+fname+"\n");
                        String res = "";
                        try {
                            res = readExternal(getContext(), fname, "GBK");
                       //     result.append(res);
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else{
                    this.search(files[j]);
                }
            }
        }
    }
    public String readExternal(Context context, String filename, String coding) throws IOException {
        StringBuilder sb = new StringBuilder("");
            //打开文件输入流
        FileInputStream inputStream = new FileInputStream(filename);
        byte[] buffer = new byte[1024];
        int len = inputStream.read(buffer);
            //读取文件内容
        while(len > 0){
            sb.append(new String(buffer,0,len,coding));
                //继续将数据放到buffer中
            len = inputStream.read(buffer);
        }
            //关闭输入流
        inputStream.close();
        return sb.toString();
    }
}