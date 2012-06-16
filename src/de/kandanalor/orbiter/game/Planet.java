package de.kandanalor.orbiter.game;

import de.kandanalor.orbiter.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

public class Planet extends GameObject {


	private int bmp_id = -1;
	private transient Bitmap img = null;
	private transient Bitmap img_orig = null;
	public Planet() {
		
	}
	public Planet(int radius, int mass, int img_id) {
		super();
		setRadius(radius);
		setMass(mass);
		setBitmapResource(img_id);
	}
	public void setBitmap(Bitmap bmp) {
		this.img_orig = bmp;
		if(img_orig != null && getRadius() > 0)
			this.img = getResizedBitmap(img_orig, getRadius()*2, getRadius()*2);
	}
	public void setBitmapResource(int id) {
		this.bmp_id = id;
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
	@Override
	public void loadBitmaps(Context context){
		if(getBitmap() == null) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), bmp_id, options);
			setBitmap(bmp);
		}
	}
	@Override
	public GameObject clone() {
		Planet clone = new Planet(getRadius(), getMass(), bmp_id);
		clone.setPos(getPos().x, getPos().y);
		clone.setMovement(getMovement().x, getMovement().y);
		
		return clone;
	}

}
