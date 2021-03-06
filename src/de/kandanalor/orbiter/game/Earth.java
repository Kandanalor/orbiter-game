package de.kandanalor.orbiter.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;
import de.kandanalor.orbiter.R;

public class Earth extends Planet {
	private static final String TAG = "Moon";

	
	public Earth() {
		setRadius(50);
		setBitmapResource(R.drawable.earth);
		setMass(81);
		setName("Earth");
		setColor(0xFF0000FF);
	}	
}
