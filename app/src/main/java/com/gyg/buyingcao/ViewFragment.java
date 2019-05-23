package com.gyg.buyingcao;

import android.content.Context;
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
    private TextView result;
    private Button button;
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
        result=(TextView)getActivity().findViewById(R.id.view_textView1);
        result.setMovementMethod(ScrollingMovementMethod.getInstance());
        button=(Button)getActivity().findViewById(R.id.view_button1);
        strCaseNum = pref.getString("CaseNum","");
        EditText key_editText = getActivity().findViewById(R.id.file_key);
        key_editText.setText(strCaseNum);
        path = Environment.getExternalStorageDirectory().getPath()+"/download/";
     //   info = getString(R.string.info);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText key_editText = getActivity().findViewById(R.id.file_key);
                key = key_editText.getText().toString();
                result.setText("");
                search(new File(path));
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
        EditText key_editText = getActivity().findViewById(R.id.file_key);
        key_editText.setText(strCaseNum);
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
                        result.append("\n"+fname+"\n");
                        String res = "";
                        try {
                            res = readExternal(getContext(), fname, "GBK");
                            result.append(res);
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