package de.kandanalor.orbiter.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import de.kandanalor.orbiter.R;
import de.kandanalor.orbiter.game.GameObject;
import de.kandanalor.orbiter.physics.Movement;

public abstract class VectorKnob extends Drawable {

	private int alpha = 255;
	protected GameObject origin = null;
	private PointF vector = null;

	
	public VectorKnob(GameObject origin) {
		super();
		this.origin = origin;
	}
	
	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(6);
		paint.setColor(GameObjectUI.ctx.getResources().getColor(R.color.mov_arrow));
		paint.setAntiAlias(true);
		
		
		PointF pos = origin.getPos();
		//der Bewegungspfeil
		
		canvas.drawCircle(pos.x, pos.y, origin.getRadius()+2, paint);

		PointF[] arrow = getArrow();
		PointF knob_point = arrow[1];
		
		canvas.drawLine(arrow[0].x, arrow[0].y , arrow[1].x, arrow[1].y, paint);
		
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		
		canvas.drawCircle(knob_point.x, knob_point.y, 10, paint);
	}
	
	public abstract PointF getValue();
	public abstract void setValue(PointF pos);
	
	public void setPosition(PointF pos) {
		PointF opos = origin.getPos();
		
		int radius = origin.getRadius();
		float dx = pos.x - opos.x;
		float dy =  pos.y - opos.y;
		PointF vector = new PointF(dx, dy);
		
		if(vector.length() > radius) {
			PointF norm = new PointF(vector.x / vector.length(), vector.y / vector.length());
			setValue(new PointF(dx - norm.x * radius, dy - norm.y * radius));
		}
		else {
			setValue(new PointF(0,0));
		}
	}
	public PointF getPosition() {
		return getArrow()[1];
	}

	private PointF[] getArrow() {
		PointF vector = getValue();
		int radius = origin.getRadius();
		PointF pos = origin.getPos();
		
		if(vector.length() > 0) {
			PointF norm = new PointF(vector.x / vector.length(), vector.y / vector.length());
			PointF end = new PointF(pos.x +vector.x, pos.y + vector.y);
			//Log.d(TAG, "Movement: " + mov.x + " " + mov.y);
			
			//strahlensatz
			float dist = (float) Math.sqrt((pos.x-end.x)*(pos.x-end.x) + (pos.y-end.y) * (pos.y-end.y));
			pos = new PointF(pos.x + norm.x * radius, pos.y + norm.y * radius);
			end = new PointF(pos.x +vector.x, pos.y + vector.y);
			return new PointF[]{pos, end};
		}
		else {
			return  new PointF[]{pos, pos};
		}
	}
	@Override
	public int getOpacity() {
		return  PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	@Override
	public void setColorFilter(ColorFilter cf) {

	}

}
