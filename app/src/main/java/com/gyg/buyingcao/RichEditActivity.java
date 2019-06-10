package com.gyg.buyingcao;

import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.wasabeef.richeditor.RichEditor;

import static java.security.AccessController.getContext;

public class RichEditActivity extends AppCompatActivity {

    private RichEditor mEditor;
    private TextView mPreview;
    private String txtFilePath = "";
    private String viewType = "";
    private TextView tvOK = null,tvQuit = null;
    private int nPagView = 1;
    private String[] pns = null;
    private String[] kws = null;
    private String[] colors = null;
    private String strCaseApd = null;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String strCaseNum=null,strCaseClass=null,strPns=null;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_edit);
        mEditor = (RichEditor) findViewById(R.id.editor);
        tvOK = (TextView)findViewById(R.id.tv_save);
        tvQuit = (TextView)findViewById(R.id.tv_quit);
        mEditor.setEditorHeight(500);
        mEditor.setEditorFontSize(25);
        mEditor.setEditorFontColor(Color.RED);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        mEditor.setPadding(10, 10, 10, 10);
        //    mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        mEditor.setPlaceholder("Insert text here...");

        mPreview = (TextView) findViewById(R.id.preview);
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {
                mPreview.setText(text);
            }
        });
        File txtFile = null;
        txtFilePath = getIntent().getStringExtra("fname");
        viewType = getIntent().getStringExtra("type");
        txtFile = new File(txtFilePath);
        try {
            if(!txtFile.exists())
                txtFile.createNewFile();
         }catch (Exception e1){
            Toast.makeText(this,e1.toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if(viewType.equals("2")) {
            //    mEditor.setEnabled(false);
            mEditor.setFocusableInTouchMode(false);
            mEditor.setVerticalScrollBarEnabled(true);
            HorizontalScrollView  horizontalScrollView = (HorizontalScrollView)findViewById(R.id.tv_toolbar);
            horizontalScrollView.setVisibility(View.GONE);
            tvOK.setVisibility(View.GONE);
            tvQuit.setVisibility(View.GONE);
            mEditor.setEditorFontColor(Color.BLACK);
        }
        if(viewType.equals("5")) {
            mEditor.setFontSize(10);
            tvOK.setVisibility(View.GONE);
            tvQuit.setVisibility(View.GONE);
            HorizontalScrollView  horizontalScrollView = (HorizontalScrollView)findViewById(R.id.tv_toolbar);
            horizontalScrollView.setVisibility(View.GONE);
            mEditor.setEditorFontColor(Color.BLACK);
        }
        if(viewType.equals("3") && txtFilePath.indexOf(".log")>0) {
            //    mEditor.setEnabled(false);
            mEditor.setFocusableInTouchMode(false);
            mEditor.setVerticalScrollBarEnabled(true);
            HorizontalScrollView  horizontalScrollView = (HorizontalScrollView)findViewById(R.id.tv_toolbar);
            horizontalScrollView.setVisibility(View.GONE);
            tvOK.setText("<= 前一页");
            tvQuit.setText("后一页 =>");
            mEditor.setEditorFontColor(Color.BLACK);
            registerClipEvents();
            pns = (new pf()).readfile(txtFilePath,"GBK");
            setText(nPagView);
        }
        if(!txtFile.exists()) {
            Toast.makeText(this,"目标文件：\n" + txtFilePath + "不存在", Toast.LENGTH_LONG).show();
            return;
        }else {
            String strTxt = "";
            try {
                strTxt = new MyUtil(getApplication()).readExternal(txtFilePath).replaceAll("\n","<br />");
                mEditor.setHtml(strTxt);
                if(viewType.equals("6")) {
                    String strCmd = getIntent().getStringExtra("cmd");
                    int n1 = strCmd.indexOf("getSipoe");
                    int n2 = strCmd.indexOf(".bat");
                    if(n2>n1 && n1>=0) {
                        if (strTxt.indexOf(strCmd.substring(n1, n2)) < 0)
                            mEditor.setHtml(strCmd);
                    }
                }
                if(viewType.equals("5")){
                    String strTxt1 = strTxt.replaceAll("</tr>","#");
                    String strTxt2 = strTxt1.replaceAll("</?[^>]+>", "");
                    String strTxt3 = strTxt2.replaceAll("#","<br />");
                    int ti = 0;
                    int tn = strTxt3.indexOf("剩余天数",ti);
                    int dtn = "剩余天数：1".length();
                    while(tn>=0){
                        int dtn1 = dtn;
                        while(Character.isDigit(strTxt3.charAt(tn+dtn1)))
                            dtn1++;
                        strTxt3 = strTxt3.substring(0,tn)+"<font color=\"red\">"+
                                strTxt3.substring(tn,tn+dtn1)+"</font>"+
                                strTxt3.substring(tn+dtn1);
                        ti=tn+dtn1+"<font color=\"red\"></font>".length();
                        tn = strTxt3.indexOf("剩余天数",ti);
                    }
                    mEditor.setHtml(strTxt3);
                }
              } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }

        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
              //  Toast.makeText(getApplication(), "viewType="+viewType+";txtFilePath="+txtFilePath, Toast.LENGTH_LONG).show();
                if(viewType.equals("3") && txtFilePath.indexOf(".log")>0) {
                    nPagView--;
                    setText(nPagView);
                    return;
                }
                try{
                    String keys1 = mEditor.getHtml();
                    List<String> regs = new ArrayList<String>();
                    List<String> reps = new ArrayList<String>();
                    regs.add("</?[^>]+>");
                    reps.add("");
                    regs.add("<a>\\s*|\t|\r\n</a>");
                    reps.add("");
                    regs.add("^\n");
                    reps.add("\n");
                    regs.add("&gt;");
                    reps.add(">");
                    regs.add("&nbsp;");
                    reps.add(" ");
                    for(int k1=0;k1<regs.size();k1++){
                        keys1 = keys1.replaceAll(regs.get(k1),reps.get(k1));
                    }
                    if(keys1.indexOf("---")>0)
                        keys1 = keys1.split("---")[0];
                    if(viewType.equals("6"))
                        keys1  = keys1.replace("sipoe.txt","sipoe.txt\n");
                    new MyUtil(getApplication()).writeTxtToFile(keys1,txtFilePath);
                    Toast.makeText(getApplication(), "已经保存", Toast.LENGTH_LONG).show();
                    finish();
                }catch (Exception e) {
                    Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_LONG).show();
                }
                if(viewType.equals("4") || viewType.equals("6")){
                    try {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        File file = new File(txtFilePath);
                        Uri uri = FileProvider7.getUriForFile(getApplication(),file);
                        intent.putExtra(Intent.EXTRA_STREAM, uri);  //传输图片或者文件 采用流的方式
                        intent.setType("*/*");   //分享文件
                        startActivity(Intent.createChooser(intent, "分享"));
                    }catch (Exception e) {
                        Toast.makeText(getApplication(),"Error on action send:\n" + e, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        tvQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(viewType.equals("3") && txtFilePath.indexOf(".log")>0) {
                    nPagView++;
                    setText(nPagView);
                    return;
                }
                finish();
            }
        });


        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setSubscript();
            }
        });

        findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setSuperscript();
            }
        });

        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(6);
            }
        });

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            boolean isChanged;

            @Override public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            boolean isChanged;

            @Override public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG","dachshund");
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
            }
        });
        findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.insertTodo();
            }
        });

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 在这里添加屏幕切换后的操作
    }
    public void setText(int index){
        int i = index;
        if(i==1)
            tvOK.setEnabled(false);
        else
            tvOK.setEnabled(true);
        int n1=0;
        int n2=0 ;
        boolean en=false;
        Pattern p=null;
        Matcher m=null;
        for(int j =0;j<pns.length;j++){
            p=Pattern.compile("^DWPI");
            m=p.matcher(pns[j]);
            if(m.find()){
                en=true;
//	        		System.out.println("n1:"+n1);
                break;
            }
        }
        String title = "";
        for(int j =0;j<pns.length;j++){
            p=Pattern.compile("^[0-9]{1,}/[0-9]{1,}");
            m=p.matcher(pns[j]);
            if(m.find()) {
                i = i - 1;
                title = m.group();
            }
            if(i<=0) {
                n1 = j;
                break;
            }
        }
        if(n1 == 0) {
            n1 = pns.length - 1;
            tvQuit.setEnabled(false);
        }else
            tvQuit.setEnabled(true);
        for(int j = n1 + 1;j<pns.length;j++){
            p=Pattern.compile("^[0-9]{1,}/[0-9]{1,}");
            m=p.matcher(pns[j]);
            if(m.find()){
                n2 = j;
                break;
            }
         }
         String content = title + "<br />";
         if(n2 == 0) {
             n2 = pns.length - 1;
             tvQuit.setEnabled(false);
         }else
             tvQuit.setEnabled(true);
   //     Toast.makeText(this,"index = " + index + ";n1 = " + n1 + ";n2 = " + n2, Toast.LENGTH_LONG).show();
        for(int j=n1+1;j<n2;j++) {
            p = Pattern.compile("^[a-zA-Z0-9-]");
            m = p.matcher(pns[j]);
            if (m.find()) {
                if (pns[j].indexOf(" DW") >= 0)
                    content += "<br />" + pns[j].replaceAll("  +", " ").replaceAll("\\t", "").replaceAll("。", "。<br />");
                else
                    content += "<br />" + pns[j].replaceAll("^  +", "").replaceAll("\\t", "").replaceAll("。", "。<br />") + " ";
            } else {
                if (pns[j].indexOf(" DW") >= 0)
                    content += pns[j].replaceAll("  +", " ").replaceAll("\\t", "").replaceAll("。", "。<br />");
                else
                    content += pns[j].replaceAll("^  +", "").replaceAll("\\t", "").replaceAll("。", "。<br />") + " ";
            }
        }
        try {
            if(kws == null)
                kws = getKwd();
        }catch (Exception e1)
        {
            Toast.makeText(this,e1.toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if(colors == null) {
            String strColors = "#FF0000,#FFFFFF,#00FFFF,#C0C0C0,#0000FF,#808080,#0000A0,#000000,#ADD8E6,#FFA500,#800080,#A52A2A,#FFFF00,#800000,#00FF00,#008000,#FF00FF,#808000";
            colors = strColors.split(",");
        }
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        if(strCaseApd == null)
            strCaseApd = pref.getString("CaseApd","");
        int m1 = 0;
        for(String kw1:kws){
            m1=(m1+1)%colors.length;
            content = content.replaceAll(kw1,"<font color=\"" + colors[m1] + "\" size = 30>" + kw1 + "</font>");
        }
        if(!strCaseApd.equals(""))
            content = "<font color=\"red\"><u>" + strCaseApd + "</u></font><br /><br />" + content;
        mEditor.setHtml(content);
        mEditor.scrollTo(0,0);
    }
    public String[] getKwd(){
        String[]  k1 = (new pf()).readfile(txtFilePath.replace(".log",".2.txt"),"GBK");
        String f3=txtFilePath.replace(".log",".3.txt");
        String f3e=txtFilePath.replace(".log",".3.e.txt");
        String[]  k2 = (new pf()).readfile(f3,"GBK");
        String[]  k2e = (new pf()).readfile(f3e,"GBK");
        List<String> klist = new ArrayList<String>();
        for(String k11:k1){
            String[] k12 = k11.split(",");
            for(String k13:k12){
                if(klist.toString().indexOf(k13) == -1){
                    klist.add(k13);
                }
            }
        }
        for(String k21:k2){
            k21 = k21.replaceAll(".*=", "");
            k21 = k21.replaceAll("\\(|\\)|or| ","");
            String[] k22 = k21.split(",");
            for(String k23:k22){
                if(klist.toString().indexOf(k23) == -1){
                    klist.add(k23);
                }
            }
        }
        for(String k21:k2e){
            k21 = k21.replaceAll(".*=", "");
            k21 = k21.replaceAll("\\(|\\)|or| ","");
            String[] k22 = k21.split(",");
            for(String k23:k22){
                if(klist.toString().indexOf(k23) == -1){
                    klist.add(k23);
                }
            }
        }
        String[] ret = new String[klist.size()];
        for(int i=0;i<klist.size();i++)
            ret[i]=klist.get(i);
        return ret;
    }
    public void registerClipEvents() {
        final ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        manager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                if (manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {
                    CharSequence addedText = manager.getPrimaryClip().getItemAt(0).getText();
                    if (addedText != null) {
                        final String pn = addedText.toString();
                        if(viewType.equals("3") && txtFilePath.indexOf(".log")>0) {
                            Toast.makeText(getApplication(),"选择文字：\n" + pn + ".0.pdf", Toast.LENGTH_SHORT).show();
                            final String patentPath = Environment.getExternalStorageDirectory().getPath()+"/download/";
                            editor = pref.edit();
                            strCaseNum = pref.getString("CaseNum","");
                            strPns = pref.getString(strCaseNum,"");
                            if(strPns.indexOf(pn)<0) {
                                editor.putString(strCaseNum, strPns + ";" + pn);
                                editor.commit();
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(RichEditActivity.this);
                            builder.setTitle("提示");    //设置对话框标题
                            builder.setIcon(android.R.drawable.btn_star);   //设置对话框标题前的图标
                            final TextView tv = new TextView(RichEditActivity.this);
                            builder.setView(tv);
                            tv.setText("选择复制的内容已经保存!\n是否要立即下载全文?");
                            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(!(new MyUtil(getApplication()).writeTxtToFile(pn,patentPath,"p"+pn + ".0.pdf")))
                                        return;
                                    Toast.makeText(getApplication(),"写文件成功：\n" + pn + ".0.pdf", Toast.LENGTH_SHORT).show();
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        File file = new File(patentPath,"p"+pn + ".0.pdf");
                                        Uri uri = FileProvider7.getUriForFile(getApplication(),file);
                                        intent.putExtra(Intent.EXTRA_STREAM, uri);  //传输图片或者文件 采用流的方式
                                        intent.setType("*/*");   //分享文件
                                        startActivity(Intent.createChooser(intent, "分享"));
                                    }catch (Exception e) {
                                        Toast.makeText(getApplication(),"Error on action send:\n" + e, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplication(), "你点了取消", Toast.LENGTH_SHORT).show();
                               //     finish();
                                }
                            });
                            builder.setCancelable(true);    //设置按钮是否可以按返回键取消,false则不可以取消
                            AlertDialog dialog = builder.create();  //创建对话框
                            dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
                            dialog.show();
                        }
                    }
                }
            }
        });
    }
}
