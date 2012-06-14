package de.kandanalor.orbiter.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class Planet extends GameObject {



	Bitmap img = null;
	Bitmap img_orig = null;
	public Planet() {
		
	}
	public Planet(int radius, int mass, Bitmap img) {
		super();
		setRadius(radius);
		setMass(mass);
		this.img_orig = img;
		setBitmap(img);
	}
	public void setBitmap(Bitmap bmp) {
		this.img_orig = bmp;
		if(img_orig != null && getRadius() > 0)
			this.img = getResizedBitmap(img_orig, getRadius()*2, getRadius()*2);
	}


	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawBitmap(img, getPos().x-getRadius(), getPos().y-getRadius(), null);
	}
	@Override
	public void setRadius(int radius) {
		super.setRadius(radius);
		if(img_orig != null && getRadius() > 0)
			setBitmap(img_orig);
	}
	public Bitmap getBitmap() {
		return img_orig;
	}

}
