package com.legaljudgements.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoBold extends TextView {

    public RobotoBold(Context context) {
        super(context);
        init(context);
    }

    public RobotoBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
        setTypeface(tf);
    }

}
