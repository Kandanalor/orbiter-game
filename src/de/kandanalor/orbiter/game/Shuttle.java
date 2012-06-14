package de.kandanalor.orbiter.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import de.kandanalor.orbiter.R;

public class Shuttle extends GameObject {
	
	private static final String TAG = "World";
	private Bitmap lander_tex = null;
	private int radius = 60;
	
	public Shuttle(Context context) {
		lander_tex = BitmapFactory.decodeResource(context.getResources(), R.drawable.lander_plain);
	}
	
	public void onDraw(Canvas canvas) {
		Log.d(TAG, "onDraw");

		canvas.drawBitmap(lander_tex, getPos().x-lander_tex.getWidth()/2, getPos().y-lander_tex.getHeight()/2, null);
	}

	@Override
	public void updatePhysics() {
		// TODO Auto-generated method stub
		
	}
}
