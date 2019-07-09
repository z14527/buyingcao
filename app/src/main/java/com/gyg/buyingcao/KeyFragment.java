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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;
import java.io.RandomAccessFile;

import static java.lang.Math.min;

public class KeyFragment extends Fragment {
    private TextView textView;
    private Button btnKeyEdit,btnKeyExpand,btnCNKeyExpandEdit,btnENKeyExpandEdit;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    String strCaseNum = "";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_key,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       // textView=(TextView)getActivity().findViewById(R.id.key_textView1);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        strCaseNum = pref.getString("CaseNum","");
        btnKeyEdit=(Button)getActivity().findViewById(R.id.case_key_edit);
        btnKeyExpand=(Button)getActivity().findViewById(R.id.case_key_expand);
        btnCNKeyExpandEdit=(Button)getActivity().findViewById(R.id.case_cn_key_expand_edit);
        btnENKeyExpandEdit=(Button)getActivity().findViewById(R.id.case_en_key_expand_edit);
        btnKeyEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new pf().viewFileByType(getContext(),strCaseNum,".2.txt","1");
            }
        });
        btnKeyExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strCaseNum = pref.getString("CaseNum","");
                String patentPath1 = new pf().getFilePathByType(strCaseNum,".2.txt");
                String patentPath2 = new pf().getFilePathByType(strCaseNum,".3.txt");
                String patentPath3 = new pf().getFilePathByType(strCaseNum,".3.e.txt");
                String zipFilePath = patentPath1.replace(".2.txt",".2.zip");
                File[] files = new File[3];
                files[0] = new File(patentPath1);
                files[1] = new File(patentPath2);
                files[2] = new File(patentPath3);

                if (!files[0].exists()) {
                    Toast.makeText(getActivity(), "文件不存在：\n" + files[0].getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!files[1].exists() || !files[2].exists()) {
                    files = new File[1];
                    files[0] = new File(patentPath1);
                }

                File f1 = new File(zipFilePath);
                new pf().zipFiles(files,f1,"");
                //   File f1 = new File(patentPath);
                if(!f1.exists()){
                    Toast.makeText(getActivity(),"文件不存在：\n" + zipFilePath, Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    Uri uri = FileProvider7.getUriForFile(getContext(),f1);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);  //传输图片或者文件 采用流的方式
                    intent.setType("*/*");   //分享文件
                    startActivity(Intent.createChooser(intent, "分享"));
                }catch (Exception e) {
                    Toast.makeText(getActivity(),"Error on action send:\n" + e, Toast.LENGTH_LONG).show();
                }
            }
        });
        btnCNKeyExpandEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new pf().viewFileByType(getContext(),strCaseNum,".3.txt","1");
            }
        });
        btnENKeyExpandEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new pf().viewFileByType(getContext(),strCaseNum,".3.e.txt","1");
              }
        });
    }
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
}
