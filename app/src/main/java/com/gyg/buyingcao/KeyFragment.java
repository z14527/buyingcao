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
import android.widget.TextView;
import android.widget.Toast;

public class KeyFragment extends Fragment {
    private TextView textView;
    private Button btnKeyDown,btnKeyExpand;
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
        btnKeyDown=(Button)getActivity().findViewById(R.id.case_key_down);
        btnKeyExpand=(Button)getActivity().findViewById(R.id.case_key_expand);
        btnKeyDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"关键词下载", Toast.LENGTH_SHORT).show();
            }
        });
        btnKeyExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"关键词扩展", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
