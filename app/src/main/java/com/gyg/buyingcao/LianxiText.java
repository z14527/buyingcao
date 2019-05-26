package com.gyg.buyingcao;

import android.widget.TextView;

/**
 * Created by Administrator on 2019/5/26.
 */

public class LianxiText extends LianxiView<TextView> {

    public static final float MIN_TEXT_SIZE = 10f;
    public static final float MAX_TEXT_SIZE = 100.0f;

    //float scale;
    float textSize;

    public LianxiText(TextView view, float scale,int vh) {
        super(view);
     //   this.scale = scale;
        setScale(scale);
        setVh(vh);
        textSize = view.getTextSize();
    }

    @Override
    protected void zoomIn() {
        rate = (textSize-scale)/textSize;
        textSize -= scale;
        if (textSize < MIN_TEXT_SIZE) {
            textSize = MIN_TEXT_SIZE;
        }
        view.setTextSize(textSize);

    }

    @Override
    protected void zoomOut() {
        rate = (textSize+scale)/textSize;
        textSize += scale;
        if (textSize > MAX_TEXT_SIZE) {
            textSize = MAX_TEXT_SIZE;
        }
        view.setTextSize(textSize);
    }


}
