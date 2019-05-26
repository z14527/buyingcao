package com.gyg.buyingcao;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class SearchFragment extends Fragment {
    private TextView textView;
    private Button btnSearchGet,btnSearchModify,btnSearchDo;
    private CheckBox cbSearchEnglish;
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
        btnSearchGet=(Button)getActivity().findViewById(R.id.case_search_get);
        btnSearchGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"生成检索式", Toast.LENGTH_SHORT).show();
            }
        });
        btnSearchModify=(Button)getActivity().findViewById(R.id.case_search_modify);
        btnSearchModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"修改检索式", Toast.LENGTH_SHORT).show();
            }
        });
        btnSearchDo=(Button)getActivity().findViewById(R.id.case_search_go);
        btnSearchDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"开始检索", Toast.LENGTH_SHORT).show();
            }
        });
        cbSearchEnglish=(CheckBox) getActivity().findViewById(R.id.case_search_english);
    }
}