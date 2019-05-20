package com.gyg.buyingcao;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import static android.content.Context.MODE_PRIVATE;


public class CaseFragment extends Fragment {
    private TextView textView;
    private Button button;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_case,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
      //  textView=(TextView)getActivity().findViewById(R.id.case_textView1);
        button=(Button)getActivity().findViewById(R.id.case_txt_down);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText case_number_editText = getActivity().findViewById(R.id.case_number);
                String case_number = case_number_editText.getText().toString();
          //      Toast.makeText(getActivity(),"下载申请文件文本\n" + "申请号：" + case_number, Toast.LENGTH_LONG).show();
                String patentPath = Environment.getExternalStorageDirectory().getPath()+"/download/";
                Toast.makeText(getActivity(),"目标文件：\n" + patentPath + case_number + ".0.txt", Toast.LENGTH_LONG).show();
                if(!writeTxtToFile(case_number,patentPath,case_number + ".0.txt",false))
                    return;
                Toast.makeText(getActivity(),"写文件成功：\n" + patentPath + case_number + ".0.txt", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(patentPath + case_number + ".0.txt")));  //传输图片或者文件 采用流的方式
                intent.setType("*/*");   //分享文件
                getContext().getApplicationContext().startActivity(Intent.createChooser(intent, "分享"));
            }
        });
    }
    // 将字符串写入到文本文件中
    private boolean writeTxtToFile(String strcontent, String filePath, String fileName,boolean append) {
        //生成文件夹之后，再生成文件，不然会出错
        if(makeFilePath(filePath, fileName)==null){
             return false;
        }
        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists() || append == false) {
              //  Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                if(append == false)
                    file.deleteOnExit();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
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
            FileInputStream fis = getContext().openFileInput(fileName);
            //获取文件长度
            int lenght = fis.available();
            byte[] buffer = new byte[lenght];
            fis.read(buffer);
            //将byte数组转换成指定格式的字符串
            result = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  result;
    }
}
