package com.gyg.buyingcao;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static java.security.AccessController.getContext;

public class AviewActivity extends AppCompatActivity {

    protected static final float FLIP_DISTANCE = 50;
    TextView textView = null;
    float oldy = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aview);
        textView = (TextView) this.findViewById(R.id.textview);
        File txtFile = null;
        String txtFilePath = "";
        final float zoomScale = 0.5f;// 缩放比例
        try {
            txtFilePath = getIntent().getStringExtra("fname");
            txtFile = new File(txtFilePath);
        }catch (Exception e1){
            Toast.makeText(this,e1.toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if(!txtFile.exists()) {
            Toast.makeText(this,"目标文件：\n" + txtFilePath + "不存在", Toast.LENGTH_LONG).show();
            return;
        }else {
            String strTxt = "";
            try {
                strTxt = readExternal(this, txtFilePath, "GBK");
                textView.setText(strTxt);
                textView.setMovementMethod(ScrollingMovementMethod.getInstance());
                textView.setVerticalScrollBarEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }

        ViewTreeObserver vto = textView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Layout layout = textView.getLayout();
                try{
                    final int vh = getViewHeight(textView);
                    textView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                                case MotionEvent.ACTION_DOWN:
                                    oldy = event.getY();
                                    //     Toast.makeText(v.getContext(),"按下第一个点\nMotionEvent.ACTION_DOWN", Toast.LENGTH_LONG).show();
                                    break;
                                case MotionEvent.ACTION_UP:
                                    if(oldy<event.getY() && event.getY()<v.getHeight()*4/5 )
                                        v.scrollTo(0,v.getScrollY()-(int)(v.getHeight()/5));
                                    if(oldy>event.getY() && event.getY()>v.getHeight()/5 )
                                        v.scrollTo(0,v.getScrollY()+(int)(v.getHeight()/5));
                                    if(event.getY()<v.getHeight()/5 && oldy>event.getY())
                                        v.scrollTo(0,0);
                                    if(event.getY()>v.getHeight()*4/5 && oldy<event.getY())
                                        v.scrollTo(0, vh);
                                    break;
                                case MotionEvent.ACTION_POINTER_UP:
                                    //   Toast.makeText(v.getContext(),"空\nMotionEvent.ACTION_UP，ACTION_POINTER_UP", Toast.LENGTH_LONG).show();
                                    break;
                                case MotionEvent.ACTION_POINTER_DOWN:
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    break;
                            }
                            return true;
                        }
                    });
                 //   new LianxiText(textView,zoomScale,vh);
                }catch (Exception e3){
                    Toast.makeText(getApplicationContext(), e3.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
     }
    private int getViewHeight(TextView pTextView) {
        Layout layout = pTextView.getLayout();
        int desired = layout.getLineTop(pTextView.getLineCount());
        int padding = pTextView.getCompoundPaddingTop() + pTextView.getCompoundPaddingBottom();
        return desired + padding;
    }
    public String readExternal(Context context, String filename, String coding) throws IOException {
        StringBuilder sb = new StringBuilder("");
        //打开文件输入流
        FileInputStream inputStream = new FileInputStream(filename);
        byte[] buffer = new byte[1024];
        int len = inputStream.read(buffer);
        //读取文件内容
        while(len > 0){
            sb.append(new String(buffer,0,len,coding));
            //继续将数据放到buffer中
            len = inputStream.read(buffer);
        }
        //关闭输入流
        inputStream.close();
        return sb.toString();
    }
}
