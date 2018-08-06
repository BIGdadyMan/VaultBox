package com.vb.vaultbox.Utills;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Manish Yadav on 7/6/2017.
 */

public class Myedittext extends EditText {

    public Myedittext(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Myedittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Myedittext(Context context) {
        super(context);
        init();
    }

    public void init() {
//        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),
//                "Montserrat.otf");
//        setTypeface(typeface ,1);
    }
}
