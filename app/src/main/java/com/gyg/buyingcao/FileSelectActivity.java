package com.gyg.buyingcao;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tranwon.android.adaptertool.MyBaseAdapterListview;
import com.tranwon.android.adaptertool.MyViewHolderExpandbleListView;
import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FileSelectActivity extends AppCompatActivity {
    private List<String> mDataList = null;
    private ListView mListView = null;
    private MyAdapter adapter;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String strCaseNum=null,strCaseClass=null,strPns=null;
    private TextView tvSelectAll = null,tvSelectReverse = null,tvSelectDelete = null,tvSelectDown = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        strCaseNum = pref.getString("CaseNum","");
        mDataList = new ArrayList<String>();
        if(!strCaseNum.equals("")){
            strPns = pref.getString(strCaseNum,"");
            if(!strPns.equals("")){
                String[] strPn1 = strPns.split(";");
                for(int i = 0; i < strPn1.length; i ++) {
                    String pn = strPn1[i];
                    Pattern p1 = Pattern.compile("^[0-9a-zA-Z]+$");
                    if(p1.matcher(pn).matches() && pn.length()>5 && pn.length()<18)
                        mDataList.add(strPn1[i]);
                }
            }
        }
        mListView = (ListView) findViewById(R.id.listView);
        adapter=new MyAdapter(mDataList,getApplication());
        mListView.setAdapter(adapter);
        tvSelectAll = (TextView)findViewById(R.id.tv_select_all);
        tvSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                adapter.checkAll();
                adapter.notifyDataSetChanged();
            }
        });
        tvSelectReverse = (TextView)findViewById(R.id.tv_select_reverse);
        tvSelectReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                adapter.checkReverse();
                adapter.notifyDataSetChanged();
            }
        });
        tvSelectDelete = (TextView)findViewById(R.id.tv_select_delect);
        tvSelectDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                adapter.checkDelete();
                adapter.notifyDataSetChanged();
            }
        });
        tvSelectDown = (TextView)findViewById(R.id.tv_select_down);
        tvSelectDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                List<String> list = adapter.getSelectText();
                String[] fs = new String[list.size()];
                for(int i=0;i<list.size();i++)
                    fs[i] = list.get(i).toString();
                String txtFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/" + "CN" + strCaseNum + ".n.pdf";
                new pf().writefile(txtFilePath,"GBK",fs);
                File f1 = new File(txtFilePath);
                if(!f1.exists()){
                    Toast.makeText(getApplication(),"文件不存在：\n" + txtFilePath, Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    Uri uri = FileProvider7.getUriForFile(getApplication(),f1);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);  //传输图片或者文件 采用流的方式
                    intent.setType("*/*");   //分享文件
                    startActivity(Intent.createChooser(intent, "分享"));
                }catch (Exception e) {
                    Toast.makeText(getApplication(),"Error on action send:\n" + e, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public class MyAdapter extends BaseAdapter {
        private List<String> listText;
        private Context context;
        private Map<Integer,Boolean> map=new HashMap<>();
        public MyAdapter(List<String> listText,Context context){
            this.listText=listText;
            this.context=context;
        }
        public void checkAll(){
            for(int i=0;i<listText.size();i++) {
                if(map.containsKey(i))
                    map.remove(i);
                map.put(i, true);
            }
        }
        public void checkReverse(){
            for(int i=0;i<listText.size();i++) {
                if(map.containsKey(i))
                    map.remove(i);
                else
                    map.put(i, true);
            }
        }
        public List<String> getSelectText(){
            return listText;
        }
        public void checkDelete(){
            strCaseNum = pref.getString("CaseNum","");
            if(!strCaseNum.equals("")){
                strPns = pref.getString(strCaseNum,"");
                if(!strPns.equals("")) {
                    for (int i = listText.size() - 1; i > -1; i--) {
                        if (map.containsKey(i)) {
                            strPns = strPns.replace(listText.get(i).toString(), "");
                            listText.remove(i);
                        }
                    }
                    map.clear();
                    editor = pref.edit();
                    editor.putString(strCaseNum, strPns);
                    editor.commit();
                }
            }
        }
        @Override
        public int getCount() {
            //return返回的是int类型，也就是页面要显示的数量。
            return listText.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView==null){
                //通过一个打气筒 inflate 可以把一个布局转换成一个view对象
                view=View.inflate(context,R.layout.listview_item,null);
            }else {
                view=convertView;//复用历史缓存对象
            }
            //单选按钮的文字
            TextView radioText=(TextView)view.findViewById(R.id.tv_check_text);
            radioText.setText(listText.get(position));
            //单选按钮
            final CheckBox checkBox=(CheckBox)view.findViewById(R.id.rb_check_button);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()){
                        if(map.containsKey(position))
                            map.remove(position);
                        map.put(position,true);

                    }else {
                        map.remove(position);

                    }
                }
            });
            final TextView textView = (TextView)view.findViewById(R.id.tv_select_view);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   String pdfFilePath = Environment.getExternalStorageDirectory().getPath()+"/download/"+listText.get(position)+".pdf";
                   File f1 = new File(pdfFilePath);
                    if(!f1.exists()){
                        Toast.makeText(getApplication(),"文件不存在：\n" + f1.getName(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //    Uri uri = FileProvider7.getUriForFile(getApplication(),f1);
                        FileProvider7.setIntentDataAndType(getApplication(),intent,"application/pdf",f1,true);
                        startActivity(intent);
                    }catch (Exception e) {
                        Toast.makeText(getApplication(),"Error on action send:\n" + e, Toast.LENGTH_LONG).show();
                    }
//                    Intent intent = new Intent(getApplication(),PDFViewActivity.class);
//                    intent.putExtra("fname",pdfFilePath);
//                    startActivity(intent);
                }
            });
            if(map!=null&&map.containsKey(position)){
                checkBox.setChecked(true);
            }else {
                checkBox.setChecked(false);
            }
            return view;
        }
    }

}
