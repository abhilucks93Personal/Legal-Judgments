package com.legaljudgements.Utils;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AvenirNextLTProDemi extends TextView {

	public AvenirNextLTProDemi(Context context) {
		super(context);
		init(context);
	}

	public AvenirNextLTProDemi(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context){
		Typeface tf = Typeface.createFromAsset(context.getAssets(),"fonts/AvenirNextLTPro-Demi.otf");
		setTypeface(tf);
	}
	
}
