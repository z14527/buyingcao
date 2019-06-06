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
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private TextView textView;
    private Button btnSearchGet,btnSearchModify,btnSearchDo,btnSearchHistoryGet;
    private CheckBox cbSearchEnglish,cbSearchFulltext;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    String strCaseNum = "";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
     //   textView=(TextView)getActivity().findViewById(R.id.search_textView1);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        cbSearchEnglish=(CheckBox) getActivity().findViewById(R.id.case_search_english);
        cbSearchFulltext=(CheckBox) getActivity().findViewById(R.id.case_search_fulltext);
        strCaseNum = pref.getString("CaseNum","");
        btnSearchGet=(Button)getActivity().findViewById(R.id.case_search_get);
        btnSearchGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strCaseNum = pref.getString("CaseNum","");
                String info = checkKwd();
                if(info.equals(""))
                    Toast.makeText(getActivity(),"可以生成检索式", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(),"无法生成检索式\n"+info, Toast.LENGTH_SHORT).show();
                String patentPath = Environment.getExternalStorageDirectory().getPath()+"/download/" + "CN" + strCaseNum.substring(0,Math.min(strCaseNum.length(),12)) + ".3.txt";
                if(cbSearchEnglish.isChecked())
                    patentPath = Environment.getExternalStorageDirectory().getPath()+"/download/" + "CN" + strCaseNum.substring(0,Math.min(strCaseNum.length(),12)) + ".3.e.txt";
                File f1 = new File(patentPath);
                if(!f1.exists()){
                    Toast.makeText(getActivity(),"文件不存在：\n" + patentPath, Toast.LENGTH_SHORT).show();
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
        btnSearchHistoryGet=(Button)getActivity().findViewById(R.id.case_search_history_get);
        btnSearchHistoryGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strCaseNum = pref.getString("CaseNum","");
                String patentPath = Environment.getExternalStorageDirectory().getPath()+"/download/" + "CN" + strCaseNum.substring(0,Math.min(strCaseNum.length(),12)) + ".9.txt";
                File f1 = new File(patentPath);
                if(!(new MyUtil(getActivity()).writeTxtToFile(strCaseNum,patentPath)))
                    return;
                if(!f1.exists()){
                    Toast.makeText(getActivity(),"文件不存在：\n" + patentPath, Toast.LENGTH_SHORT).show();
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
        btnSearchModify=(Button)getActivity().findViewById(R.id.case_search_modify);
        btnSearchModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strCaseNum = pref.getString("CaseNum","");
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,12)+".4.txt";
                if(cbSearchEnglish.isChecked())
                    txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,12)+".4.e.txt";
                String[] jss = new pf().readfile(txtFilePath,"GBK");
                String caseApd = pref.getString("CaseApd","");
                String caseClass = pref.getString("CaseClass","");
                String ap1 = caseApd.substring(0, 4) + "-" + caseApd.substring(4,6) + "-" + caseApd.substring(6, 8);
                ap1 = ap1.replaceAll("-0","-");
                for(int i = 0; i < jss.length; i++){
                    if(!caseApd.equals("") && jss[i].indexOf("pd<=")>=0) {
                        String jss1 = jss[i].substring(0, jss[i].indexOf("="));
                        jss[i] = jss1 + ap1 + " |";
                    }
                    if(!caseClass.equals("") && jss[i].indexOf("/ic or ")>=0) {
                        String jss1 = "/ic or " + caseClass + "," + caseClass + " |";
                        jss1 = jss1.replaceAll(",,",",");
                        jss1 = jss1.replaceAll("，",",");
                        jss[i] = jss1;
                    }
                    if(cbSearchFulltext.isChecked() && jss[i].indexOf("..fi ")>=0) {
                        if (cbSearchEnglish.isChecked())
                            jss[i] = "..fi ustxt |";
                        else
                            jss[i] = "..fi cntxt |";
                    }else if(!cbSearchFulltext.isChecked() && jss[i].indexOf("..fi ")>=0){
                        if (cbSearchEnglish.isChecked())
                            jss[i] = "..fi dwpi |";
                        else
                            jss[i] = "..fi cnabs |";
                    }
                }
                new pf().writefile(txtFilePath,"GBK",jss);
                Intent intent = new Intent(getContext(),RichEditActivity.class);
                intent.putExtra("fname",txtFilePath);
                intent.putExtra("type","0");
                startActivity(intent);
            }
        });
        btnSearchDo=(Button)getActivity().findViewById(R.id.case_search_go);
        btnSearchDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strCaseNum = pref.getString("CaseNum","");
                String patentPath = Environment.getExternalStorageDirectory().getPath()+"/download/" + "CN" + strCaseNum.substring(0,Math.min(strCaseNum.length(),12)) + ".4.txt";
                if(cbSearchEnglish.isChecked())
                    patentPath = Environment.getExternalStorageDirectory().getPath()+"/download/" + "CN" + strCaseNum.substring(0,Math.min(strCaseNum.length(),12)) + ".4.e.txt";
                File f1 = new File(patentPath);
                if(!f1.exists()){
                    Toast.makeText(getActivity(),"文件不存在：\n" + patentPath, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(),"开始检索", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public String checkKwd(){
        String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,12);
        File f1 = new File(txtFilePath+".2.txt");
        if(!f1.exists())
            return "找不到文件："+txtFilePath+".2.txt";
        File f2 = new File(txtFilePath+".3.txt");
        if(!f1.exists())
            return "找不到文件："+txtFilePath+".3.txt";
        String[]  k1 = (new pf()).readfile(txtFilePath+".2.txt","GBK");
        String[]  k2 = (new pf()).readfile(txtFilePath+".3.txt","GBK");
        List<String> klist = new ArrayList<String>();
        String ret = "";
        for(String k11:k1){
            k11 = k11.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll(" [0-9sdwSDW]+ ", ",").replaceAll(" ","").replaceAll(",+",",");
            String[] k12 = k11.split(",");
            for(String k13:k12){
                if(klist.toString().indexOf(k13) == -1){
                    klist.add(k13);
                }
            }
        }
        List<String> klist2 = new ArrayList<String>();
        for(String k21:k2){
            k21 = k21.replaceAll(".*=", "");
            k21 = k21.replaceAll("\\(|\\)|or| ","");
            String[] k22 = k21.split(",");
            for(String k23:k22){
                if(klist2.toString().indexOf(k23) == -1){
                    klist2.add(k23);
                }
            }
        }
        for(int i=0;i<klist2.size();i++){
            for(int j=0;j<klist.size();j++){
                if(klist2.get(i).indexOf(klist.get(j))>=0 && klist2.get(i).length() > klist.get(j).length())
                {
                    ret = "扩展词：" + klist2.get(i) + "\n关键词：" + klist.get(j);
                    return ret;
                }
                if(klist.get(j).indexOf(klist2.get(i))>=0 && klist2.get(i).length() < klist.get(j).length())
                {
                    ret = "扩展词：" + klist2.get(i) + "\n关键词：" + klist.get(j);
                    return ret;
                }

            }
        }
        return ret;
    }
}