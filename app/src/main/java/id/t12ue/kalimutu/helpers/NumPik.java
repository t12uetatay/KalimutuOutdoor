package id.t12ue.kalimutu.helpers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

public class NumPik extends NumberPicker {

	public NumPik(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void addView(View child) {
		super.addView(child);
		updateView(child);
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		updateView(child);
	}

	@Override
	public void addView(View child, ViewGroup.LayoutParams params) {
		super.addView(child, params);
		updateView(child);
	}

	private void updateView(View view) {
		if(view instanceof EditText){
			TextView txt= (EditText) view;
			txt.setTextSize(25);
			txt.setTypeface(null, Typeface.BOLD);
			//txt.setTextColor(Color.parseColor("#333333"));
		}
	}

}
