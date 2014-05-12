package de.unipotsdam.nexplorer.client.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class PacketView extends ImageView{
	
    public PacketView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
     return super.onTouchEvent(event);
    }

}
