package com.legaljudgements.Utils;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AvenirNextLTProRegular extends TextView {

	public AvenirNextLTProRegular(Context context) {
		super(context);
		init(context);
	}

	public AvenirNextLTProRegular(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context){
		Typeface tf = Typeface.createFromAsset(context.getAssets(),"fonts/AvenirNextLTPro-Regular.otf");
		setTypeface(tf);
	}
	
}
