package de.kandanalor.orbiter.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Log;
import de.kandanalor.orbiter.R;
import de.kandanalor.orbiter.exceptions.ObjectCollisionException;

public class Moon extends Planet{
	
	private static final String TAG = "Moon";
	
	public Moon(Context context) {
		super();
		setRadius(30);
		setMass(1);
		setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.moon));
		setName("Moon");
	}
}
