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

public class KeyFragment extends Fragment {
    private TextView textView;
    private Button btnKeyEdit,btnKeyExpand;
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
        btnKeyEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String patentPath = Environment.getExternalStorageDirectory().getPath()+"/download/";
                try {
                    File file = new File(patentPath,"CN"+strCaseNum.substring(0,strCaseNum.length()-1) + ".2.txt");
                    if(!file.exists()){
                        Toast.makeText(getActivity(),"找不到文件:\n" + "CN"+strCaseNum.substring(0,strCaseNum.length()-1) + ".2.txt", Toast.LENGTH_LONG).show();
                        return;
                    }
                  //  Uri uri = FileProvider7.getUriForFile(getContext(),file);
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //设置intent的Action属性
                    intent.setAction(Intent.ACTION_VIEW);
                    //获取文件file的MIME类型
                    String type = "text/plain";
                    //设置intent的data和Type属性。
                    Uri uri = FileProvider7.getUriForFile(getContext(),file);
                    intent.setDataAndType(uri, type);
                    startActivity(intent);
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    String type = "text/*";
//                    FileProvider7.setIntentDataAndType(getContext(),intent,type,file,true);
//                //    intent.setDataAndType(uri,type);
//                    startActivity(intent);
                }catch (Exception e) {
                    Toast.makeText(getActivity(),"Error on action send:\n" + e, Toast.LENGTH_LONG).show();
                }
            }
        });
        btnKeyExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String patentPath = Environment.getExternalStorageDirectory().getPath()+"/download/";
                if(!writeTxtToFile(strCaseNum,patentPath,"k"+strCaseNum + ".2.txt"))
                    return;
                Toast.makeText(getActivity(),"写文件成功：\nk" + strCaseNum + ".2.txt", Toast.LENGTH_SHORT).show();
                try {
//                    Intent intent = new Intent();
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    //设置intent的Action属性
//                    intent.setAction(Intent.ACTION_VIEW);
//                    //获取文件file的MIME类型
//                    String type = "text/plain";
//                    //设置intent的data和Type属性。
//                    File file = new File(patentPath,"k"+strCaseNum + ".2.txt");
//                    Uri uri = FileProvider7.getUriForFile(getContext(),file);
//                    intent.setDataAndType(uri, type);
//                    startActivity(intent);

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    File file = new File(patentPath,"k"+strCaseNum + ".2.txt");
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
