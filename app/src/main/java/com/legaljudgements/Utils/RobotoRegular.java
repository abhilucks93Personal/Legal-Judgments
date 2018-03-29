package com.legaljudgements.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoRegular extends TextView {

	public RobotoRegular(Context context) {
		super(context);
		init(context);
	}

	public RobotoRegular(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context){
		Typeface tf = Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Regular.ttf");
		setTypeface(tf);
	}
	
}
