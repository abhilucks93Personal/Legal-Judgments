package com.legaljudgements.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class RobotoLightEditText extends EditText {

	public RobotoLightEditText(Context context) {
		super(context);
		init(context);
	}

	public RobotoLightEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context){
		Typeface tf = Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Light.ttf");
		setTypeface(tf);
	}
	
}
