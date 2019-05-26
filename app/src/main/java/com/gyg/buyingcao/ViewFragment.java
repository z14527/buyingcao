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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewFragment extends Fragment {
   // private TextView result;
    private Button btnCaseTxt,btnCaseKey,btnCaseKeyExpand,btnCaseSearch,btnCaseSearchRunHistory,btnCaseAbstractEnglishDown,btnCaseAbstractChineseDown,btnCaseResultView;
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
     //   info = getString(R.string.info);
   /*     button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText key_editText = getActivity().findViewById(R.id.file_key);
                key = key_editText.getText().toString();
                result.setText("");
                search(new File(path));
            }
        });*/
        btnCaseTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,12)+".txt";
                File txtFile =new File(txtFilePath);
                if(!txtFile.exists()) {
                    Toast.makeText(getActivity(),"目标文件：\n" + txtFilePath + "不存在", Toast.LENGTH_LONG).show();
                    return;
                }
                String strTxt = "";
                try {
                    strTxt = readExternal(getContext(),txtFilePath,"GBK");
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),e.toString(), Toast.LENGTH_LONG).show();
                }
                //result.setText(strTxt);
            }
        });

        btnCaseKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum+".2.txt";
                File txtFile =new File(txtFilePath);
                if(!txtFile.exists()) {
                    Toast.makeText(getActivity(),"目标文件：\n" + txtFilePath + "不存在", Toast.LENGTH_LONG).show();
                    return;
                }
                String strTxt = "";
                try {
                    strTxt = readExternal(getContext(),txtFilePath,"GBK");
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),e.toString(), Toast.LENGTH_LONG).show();
                }
              //  result.setText(strTxt);
            }
        });
        btnCaseKeyExpand=(Button)getActivity().findViewById(R.id.case_key_expand_view);
        btnCaseKeyExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"关键词扩展查看", Toast.LENGTH_SHORT).show();
            }
        });
        btnCaseSearch=(Button)getActivity().findViewById(R.id.case_search_view);
        btnCaseSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"检索式查看", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),AviewActivity.class);
                startActivity(intent);
            }
         });
        btnCaseSearchRunHistory=(Button)getActivity().findViewById(R.id.case_search_run_history_view);
        btnCaseSearchRunHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"检索历史查看", Toast.LENGTH_SHORT).show();
            }
        });
        btnCaseAbstractChineseDown=(Button)getActivity().findViewById(R.id.case_abstract_chinese_down);
        btnCaseAbstractChineseDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"下载中文摘要", Toast.LENGTH_SHORT).show();
            }
        });
        btnCaseAbstractEnglishDown=(Button)getActivity().findViewById(R.id.case_abstract_english_down);
        btnCaseAbstractEnglishDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"下载英文摘要", Toast.LENGTH_SHORT).show();
            }
        });
        btnCaseResultView=(Button)getActivity().findViewById(R.id.case_search_result_view);
        btnCaseResultView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"概览", Toast.LENGTH_SHORT).show();
            }
        });
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
//        strCaseNum = pref.getString("CaseNum","");
//        EditText key_editText = getActivity().findViewById(R.id.file_key);
//        key_editText.setText(strCaseNum);
//    }
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