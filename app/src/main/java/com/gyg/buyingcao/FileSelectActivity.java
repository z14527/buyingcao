package com.gyg.buyingcao;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileSelectActivity extends AppCompatActivity {
    private List<String> mDataList = null;
    private ListView mListView = null;
    private MyAdapter adapter;
    private SharedPreferences pref;
    private String strCaseNum=null,strCaseClass=null,strPns=null;
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
                    if (strPn1[i].length() > 1)
                        mDataList.add(strPn1[i]);
                }
            }
        }
        mListView = (ListView) findViewById(R.id.listView);
        adapter=new MyAdapter(mDataList,getApplication());
        mListView.setAdapter(adapter);
    }
    public class MyAdapter extends BaseAdapter {
        private List<String> listText;
        private Context context;
        private Map<Integer,Boolean> map=new HashMap<>();
        public MyAdapter(List<String> listText,Context context){
            this.listText=listText;
            this.context=context;
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
                        map.put(position,true);

                    }else {
                        map.remove(position);

                    }
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
