package com.donz.n0te;

import android.content.Context;
import android.util.AttributeSet;

public class FastScrollView extends android.widget.ScrollView{

    public FastScrollView(Context context) {
        super(context);
    }

    public FastScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FastScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FastScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void fling(int velocityY){
        super.fling((int)(velocityY * 1.5));
    }
}
