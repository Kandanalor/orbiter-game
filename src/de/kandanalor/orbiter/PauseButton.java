package de.kandanalor.orbiter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class PauseButton extends ImageButton implements OnClickListener {
	
	public static final boolean PLAY = true;
	public static final boolean PAUSE = false;
	boolean state = PAUSE;
	
	Bitmap pause_img = null;
	Bitmap play_img = null;
	
	public PauseButton(Context context) {
		super(context);
		init();
	}
	public PauseButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public PauseButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	private void init(){
	    setOnClickListener(this);
	    
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		pause_img = BitmapFactory.decodeResource(getResources(), R.drawable.av_pause, options);
		play_img = BitmapFactory.decodeResource(getResources(), R.drawable.av_play, options);
	}
	@Override
	public void onDraw(Canvas c) {
		setImageBitmap(state ? play_img : pause_img);
		super.onDraw(c);
	}
	@Override
	public void onClick(View v) {
		setState(!state);
	}
	public boolean getState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}




}
