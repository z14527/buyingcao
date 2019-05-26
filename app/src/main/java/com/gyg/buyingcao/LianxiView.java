package com.gyg.buyingcao;

import android.view.MotionEvent;
import android.view.View;

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
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > oldDist) {
                                zoomIn();
                            }
                            if (newDist < oldDist) {
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
