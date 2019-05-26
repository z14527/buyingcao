package com.gyg.buyingcao;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by Administrator on 2019/5/26.
 */

public abstract class LianxiView<V extends View> {
    protected V view;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;

    private int mode = NONE;
    float oldDist;

    public LianxiView(V view) {
        this.view = view;
        setTouchListener();
    }

    private void setTouchListener() {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                   //     Toast.makeText(v.getContext(),"按下第一个点\nMotionEvent.ACTION_DOWN", Toast.LENGTH_LONG).show();
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                     //   Toast.makeText(v.getContext(),"空\nMotionEvent.ACTION_UP，ACTION_POINTER_UP", Toast.LENGTH_LONG).show();
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                          //  Toast.makeText(v.getContext(),"按下第二个点\nACTION_POINTER_DOWN", Toast.LENGTH_LONG).show();
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist < oldDist) {
                         //       Toast.makeText(v.getContext(),"缩小\nACTION_MOVE", Toast.LENGTH_LONG).show();
                                zoomIn();
                            }
                            if (newDist > oldDist) {
                         //       Toast.makeText(v.getContext(),"放大\nACTION_MOVE", Toast.LENGTH_LONG).show();
                                zoomOut();
                            }
                        }
                        break;
                }
                return true;
            }

            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return (float) Math.sqrt(x * x + y * y);
            }
        });
    }

    protected abstract void zoomIn();

    protected abstract void zoomOut();
}
