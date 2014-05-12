package de.unipotsdam.nexplorer.client.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class PackageFooterActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		HorizontalScrollView sv = new HorizontalScrollView(this);
		LinearLayout ll = new LinearLayout(this);
		
		sv.setBackgroundColor(Color.parseColor("#C9DAEF"));
		sv.setPadding(10, 10, 0, 10);
		
		ll.setOrientation(LinearLayout.HORIZONTAL);
		sv.addView(ll);
	}
}
