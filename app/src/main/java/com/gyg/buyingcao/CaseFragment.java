package com.gyg.buyingcao;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private Button btnImport,btnOK,btnClear,btnExec,btnSetAccountSx,btnGetRealSx,btnDSCAJ,btnResetEA,btnHistory;
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
        if(strCaseNum.equals("")) {
            String strCaseNumInfo = pref.getString("CaseNumInfo", "");
            if (!strCaseNumInfo.equals("")) {
                int i1 = strCaseNumInfo.indexOf(";");
                if (i1 >= 0)
                    strCaseNum = strCaseNumInfo.substring(0, i1);
                else
                    strCaseNum = strCaseNumInfo;
            }
        }
        if(!strCaseNum.equals("")) {
            strCaseApd = pref.getString(strCaseNum+"-CaseApd","");
            strCaseClass = pref.getString(strCaseNum+"-CaseClass","");
        }
        numEText=(EditText)getActivity().findViewById(R.id.case_number);
        apdEText=(EditText)getActivity().findViewById(R.id.case_date);
        classEText=(EditText)getActivity().findViewById(R.id.case_class);
        numEText.setText(strCaseNum);
        apdEText.setText(strCaseApd);
        classEText.setText(strCaseClass);
        btnImport=(Button)getActivity().findViewById(R.id.case_import);
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strCaseNum = numEText.getText().toString();
                String patentTxTPath = new pf().getFilePathByType(strCaseNum,"-著录项目.txt");
                File txtFile = new File(patentTxTPath);
                if (!txtFile.exists()) {
                    Toast.makeText(getActivity(), "目标文件：\n" + patentTxTPath + "不存在", Toast.LENGTH_LONG).show();
                    return;
                }
                //      Toast.makeText(getActivity(),"找到目标文件：\n" + patentTxTPath , Toast.LENGTH_LONG).show();
                String strApd = "";
                String strIc = "";
                try {
                    String[] zlxm = new pf().readfile(patentTxTPath, "GBK");
                    if (zlxm.length >= 2) {
                        String[] pTxt1 = zlxm[0].split("=");
                        if (pTxt1.length > 1)
                            strApd = pTxt1[1];
                        String[] pTxt2 = zlxm[1].split("=");
                        if (pTxt2.length > 1)
                            strIc = pTxt2[1];
                        apdEText.setText(strApd);
                        classEText.setText(strIc);
                    }
                } catch (Exception e1) {
                    Toast.makeText(getActivity(), e1.toString(), Toast.LENGTH_LONG).show();
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
                String strCaseNumInfo = pref.getString("CaseNumInfo","");
                if(strCaseNumInfo.indexOf(strCaseNum)<0) {
                    if (strCaseNumInfo.equals(""))
                        strCaseNumInfo = strCaseNum;
                    else
                        strCaseNumInfo = strCaseNum + ";" + strCaseNumInfo;
                }
                editor.putString("CaseNumInfo",strCaseNumInfo);
                editor.putString("CaseNum",strCaseNum);
                editor.putString(strCaseNum+"-CaseApd",strCaseApd);
                editor.putString(strCaseNum+"-CaseClass",strCaseClass);
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
        btnHistory=(Button)getActivity().findViewById(R.id.case_history);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strCaseNumInfo = pref.getString("CaseNumInfo", "");
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                //builder.setIcon(R.drawable.ic_launcher);
                builder.setIcon(android.R.drawable.btn_star);
                if (!strCaseNumInfo.equals(""))
                {
                    String[] nStr1 = strCaseNumInfo.split(";");
                    if(nStr1.length>12) {
                        String str1 = "";
                        for (int k = 0; k < 12; k++) {
                            if (!nStr1[k].equals("")) {
                                if (str1.equals(""))
                                    str1 = nStr1[k];
                                else
                                    str1 = str1 + ";" + nStr1[k];
                            }
                        }
                        editor = pref.edit();
                        editor.putString("CaseNumInfo", str1);
                        editor.commit();
                    }
                    strCaseNumInfo = pref.getString("CaseNumInfo", "");
                    final String[] nStrCaseNum = strCaseNumInfo.split(";");
                     //设置对话框标题前的图标
                    builder.setTitle("选择一个历史案例");
                    builder.setItems(nStrCaseNum, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                strCaseNum = nStrCaseNum[which];
                                Toast.makeText(getContext(), "选择的历史案例为：" + strCaseNum, Toast.LENGTH_SHORT).show();
                                if(!strCaseNum.equals("")) {
                                    strCaseApd = pref.getString(strCaseNum+"-CaseApd","");
                                    strCaseClass = pref.getString(strCaseNum+"-CaseClass","");
                                }
                                numEText.setText(strCaseNum);
                                apdEText.setText(strCaseApd);
                                classEText.setText(strCaseClass);
                                btnOK.callOnClick();
                            }
                        });
                } else {
                    builder.setMessage("无历史数据案例选择");
                }
                AlertDialog dialog = builder.create();  //创建对话框
                dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
                dialog.show();
            }
        });
        btnExec=(Button)getActivity().findViewById(R.id.case_exec);
        btnExec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+".c.txt";
                if(strCaseNum.length()<12 && strCaseNum.length()>=10)
                    txtFilePath = Environment.getExternalStorageDirectory().getPath() + "/download/" + "PCT-CN" + strCaseNum.substring(0, 4) + "-" + strCaseNum.substring(4, 10) + ".c.txt";
                Intent intent = new Intent(getContext(),RichEditActivity.class);
                intent.putExtra("fname",txtFilePath);
                intent.putExtra("type","4");
                intent.putExtra("cmd","");
                startActivity(intent);
            }
        });

        btnDSCAJ=(Button)getActivity().findViewById(R.id.case_get_exam);
        btnDSCAJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+"CN"+strCaseNum.substring(0,min(strCaseNum.length(),12))+".c.txt";
                String strCmd = "echo \"" + strCaseNum +
                        "\" > d:\\temp\\sipoe.txt \n" +
                        "d:\\workspace\\sipoe0605\\getSipoeWX.bat d:\\temp\\sipoe.txt";
                if(strCaseNum.length()<12 && strCaseNum.length()>=10) {
                    txtFilePath = Environment.getExternalStorageDirectory().getPath() + "/download/" + "PCT-CN" + strCaseNum.substring(0, 4) + "-" + strCaseNum.substring(4, 10) + ".c.txt";
                    String PCTN = "PCT/CN"+strCaseNum.substring(0,4)+"/"+strCaseNum.substring(4,10);
                    strCmd = "echo \"" + PCTN +
                            "\" > d:\\temp\\sipoe.txt \n" +
                            "d:\\workspace\\sipoe0605\\getSipoePCT.bat d:\\temp\\sipoe.txt";
                }
                Intent intent = new Intent(getContext(),RichEditActivity.class);
                intent.putExtra("fname",txtFilePath);
                intent.putExtra("type","6");
                 intent.putExtra("cmd",strCmd);
                startActivity(intent);
            }
        });

        btnSetAccountSx=(Button)getActivity().findViewById(R.id.case_set_account_sx);
        btnSetAccountSx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                final String[] nStrSXAccount = new String[6];
                for(int i=0;i<nStrSXAccount.length;i++)
                    nStrSXAccount[i]="";
                String str1 = pref.getString("SXs", "").replaceAll(" ","").replaceAll(";;",";");
                if(str1.indexOf(";")==0 && str1.length()>1)
                    str1 = str1.substring(1);
                final String strSxs = str1;
                if (!strSxs.equals(""))
                {
                    final String[] nStrSX= (strSxs+";"+"新增").split(";");
                    //设置对话框标题前的图标
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    //builder.setIcon(R.drawable.ic_launcher);
                    builder.setIcon(android.R.drawable.btn_star);                    builder.setTitle("选择一个实审账号");
                    builder.setItems(nStrSX, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String strSX = nStrSX[which];
                            Toast.makeText(getContext(), "选择的账号为：" + strSX, Toast.LENGTH_SHORT).show();
                            if(!strSX.equals("新增")) {
                                String strSXAccount = pref.getString(strSX,"");
                                if(!strSXAccount.equals("")){
                                    String[] nStrSXAccount1 = strSXAccount.split(";");
                                    for(int k=0;k<nStrSXAccount1.length && k<nStrSXAccount.length;k++)
                                        nStrSXAccount[k] = nStrSXAccount1[k];
                                    setSXAccount(nStrSXAccount,strSxs);
                                }
                            }else
                                setSXAccount(nStrSXAccount,strSxs);
                        }
                    });
                    AlertDialog dialog = builder.create();  //创建对话框
                    dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
                    dialog.show();
                } else {
                    setSXAccount(nStrSXAccount,strSxs);
                }
            }
        });

        btnGetRealSx=(Button)getActivity().findViewById(R.id.case_get_real_sx);
        btnGetRealSx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strCaseNum = numEText.getText().toString();
                pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                final String[] nStrSXAccount = new String[6];
                for (int i = 0; i < nStrSXAccount.length; i++)
                    nStrSXAccount[i] = "000000";
                String str1 = pref.getString("SXs", "").replaceAll(" ","").replaceAll(";;",";");
                if(str1.indexOf(";")==0 && str1.length()>1)
                    str1 = str1.substring(1);
                final String strSxs = str1;
                if (!strSxs.equals("")) {
                    final String[] nStrSX = strSxs.split(";");
                    //设置对话框标题前的图标
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    //builder.setIcon(R.drawable.ic_launcher);
                    builder.setIcon(android.R.drawable.btn_star);
                    builder.setTitle("选择一个实审账号");
                    builder.setItems(nStrSX, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String strSX = nStrSX[which];
                      //      Toast.makeText(getContext(), "选择的账号为：" + strSX, Toast.LENGTH_SHORT).show();
                            if (!strSX.equals("")) {
                                String strSXAccount = pref.getString(strSX, "");
                                if (!strSXAccount.equals("")) {
                                    String[] nStrSXAccount1 = strSXAccount.split(";");
                                    for (int k = 0; k < nStrSXAccount1.length && k < nStrSXAccount.length; k++)
                                        nStrSXAccount[k] = nStrSXAccount1[k];
                                    String en = nStrSXAccount[0];
                                    String ep = nStrSXAccount[1];
                                    String pn = nStrSXAccount[2];
                                    String pp = nStrSXAccount[3];
                                    String fn = nStrSXAccount[4];
                                    String fp = nStrSXAccount[5];
                                    String patentPath = Environment.getExternalStorageDirectory().getPath() + "/download/";
                                    String info = "d:\\workspace\\sipoe0605\\getSx.bat " + en + " " + ep + " " + pn + " " + pp + " " + fn + " " + fp;
                                    if (!(new MyUtil(getActivity()).writeTxtToFile(info, patentPath, "p" + strCaseNum + ".s.txt")))
                                        return;
                                    Toast.makeText(getActivity(), "写文件成功：\n" + strCaseNum + ".s.txt", Toast.LENGTH_SHORT).show();
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        File file = new File(patentPath, "p" + strCaseNum + ".s.txt");
                                        Uri uri = FileProvider7.getUriForFile(getContext(), file);
                                        intent.putExtra(Intent.EXTRA_STREAM, uri);  //传输图片或者文件 采用流的方式
                                        intent.setType("*/*");   //分享文件
                                        startActivity(Intent.createChooser(intent, "分享"));
                                    } catch (Exception e) {
                                        Toast.makeText(getActivity(), "Error on action send:\n" + e, Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();  //创建对话框
                    dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
                    dialog.show();
                }
            }
        });
        btnResetEA=(Button)getActivity().findViewById(R.id.case_reset_ea);
        btnResetEA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strCaseNum = numEText.getText().toString();
                String patentPath = Environment.getExternalStorageDirectory().getPath()+"/download/";
                if(!(new MyUtil(getActivity()).writeTxtToFile(strCaseNum,patentPath,"p"+strCaseNum + ".x.txt")))
                    return;
                Toast.makeText(getActivity(),"写文件成功：\n" + strCaseNum + ".x.txt", Toast.LENGTH_SHORT).show();
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    File file = new File(patentPath,"p"+strCaseNum + ".x.txt");
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
    public void setSXAccount(String[] nStrSXAcount,final String strSxs){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("提示");    //设置对话框标题
        builder.setIcon(android.R.drawable.btn_star);   //设置对话框标题前的图标
        final EditText enEdit = new EditText(getContext());
        final EditText epEdit = new EditText(getContext());
        final EditText pnEdit = new EditText(getContext());
        final EditText ppEdit = new EditText(getContext());
        final EditText fnEdit = new EditText(getContext());
        final EditText fpEdit = new EditText(getContext());
        final TextView enTv = new TextView(getContext());
        final TextView epTv = new TextView(getContext());
        final TextView pnTv = new TextView(getContext());
        final TextView ppTv = new TextView(getContext());
        final TextView fnTv = new TextView(getContext());
        final TextView fpTv = new TextView(getContext());
        enTv.setText("   E系统用户名：");
        epTv.setText("   E系统密码：");
        pnTv.setText("   PCT系统用户名：");
        ppTv.setText("   PCT系统密码：");
        fnTv.setText("   复审系统用户名：");
        fpTv.setText("   复审系统密码：");
        enTv.setWidth(400);
        epTv.setWidth(400);
        pnTv.setWidth(400);
        ppTv.setWidth(400);
        fnTv.setWidth(400);
        fpTv.setWidth(400);
        enEdit.setWidth(500);
        epEdit.setWidth(500);
        pnEdit.setWidth(500);
        ppEdit.setWidth(500);
        fnEdit.setWidth(500);
        fpEdit.setWidth(500);
        LinearLayout layout=new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout layout1=new LinearLayout(getContext());
        layout1.setOrientation(LinearLayout.HORIZONTAL);
        layout1.addView(enTv);
        layout1.addView(enEdit);
        LinearLayout layout2=new LinearLayout(getContext());
        layout2.setOrientation(LinearLayout.HORIZONTAL);
        layout2.addView(epTv);
        layout2.addView(epEdit);
        LinearLayout layout3=new LinearLayout(getContext());
        layout3.setOrientation(LinearLayout.HORIZONTAL);
        layout3.addView(pnTv);
        layout3.addView(pnEdit);
        LinearLayout layout4=new LinearLayout(getContext());
        layout4.setOrientation(LinearLayout.HORIZONTAL);
        layout4.addView(ppTv);
        layout4.addView(ppEdit);
        LinearLayout layout5=new LinearLayout(getContext());
        layout5.setOrientation(LinearLayout.HORIZONTAL);
        layout5.addView(fnTv);
        layout5.addView(fnEdit);
        LinearLayout layout6=new LinearLayout(getContext());
        layout6.setOrientation(LinearLayout.HORIZONTAL);
        layout6.addView(fpTv);
        layout6.addView(fpEdit);
        layout.addView(layout1);
        layout.addView(layout2);
        layout.addView(layout3);
        layout.addView(layout4);
        layout.addView(layout5);
        layout.addView(layout6);
        builder.setView(layout);
        enEdit.setText(nStrSXAcount[0]);
        epEdit.setText(nStrSXAcount[1]);
        pnEdit.setText(nStrSXAcount[2]);
        ppEdit.setText(nStrSXAcount[3]);
        fnEdit.setText(nStrSXAcount[4]);
        fpEdit.setText(nStrSXAcount[5]);
        epEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        ppEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        fpEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String en = enEdit.getText().toString();
                String ep = epEdit.getText().toString();
                String pn = pnEdit.getText().toString();
                String pp = ppEdit.getText().toString();
                String fn = fnEdit.getText().toString();
                String fp = fpEdit.getText().toString();
                editor = pref.edit();
                String strSxs1 =  strSxs;
                if(!strSxs.contains(en)){
                    if(strSxs.equals(""))
                        strSxs1 = en;
                    else
                        strSxs1 = strSxs1+";"+en;
                }
                editor.putString("SXs",strSxs1);
                String strSXAccount = en+";"+ep+";"+pn+";"+pp+";"+fn+";"+fp;
                editor.putString(en,strSXAccount);
                editor.commit();
            }
        });
        builder.setNeutralButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String en = enEdit.getText().toString();
                String ep = epEdit.getText().toString();
                String pn = pnEdit.getText().toString();
                String pp = ppEdit.getText().toString();
                String fn = fnEdit.getText().toString();
                String fp = fpEdit.getText().toString();
                if(!en.equals("")) {
                    String strSxs = pref.getString("SXs", "");
                    if (strSxs.contains(en)) {
                        strSxs = strSxs.replace(en + ";", "");
                        strSxs = strSxs.replace(";" + en, ";");
                        strSxs = strSxs.replace(en, ";");
                        editor = pref.edit();
                        editor.putString("SXs", strSxs);
                        editor.remove(en);
                        editor.commit();
                    }
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "你点了取消", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setCancelable(true);    //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create();  //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }

}
