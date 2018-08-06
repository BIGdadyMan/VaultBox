package com.vb.vaultbox.Utills;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Ajay on 10/5/2017.
 */
public class Mytetview extends TextView {

    public Mytetview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Mytetview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Mytetview(Context context) {
        super(context);
        init();
    }

    public void init() {
//        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),"Montserrat.otf");
//        setTypeface(typeface ,1);

    }
}
