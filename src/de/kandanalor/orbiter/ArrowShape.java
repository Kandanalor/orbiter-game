package de.kandanalor.orbiter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.shapes.Shape;

public class ArrowShape extends Shape {
	
	PointF start = null;
	PointF end = null;
	float cap_degr = 30;
	
	public ArrowShape(PointF start, PointF vector) {
		super();
		this.start = start;
		this.end = new PointF(start.x + vector.x, start.y + vector.y );
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		canvas.drawLine(start.x, start.y, end.x, end.y, paint);
		canvas.drawLine(start.x - (start.y-end.y)/10, start.y - (start.x-end.x)/10, start.x + (start.y-end.y)/10, start.y + (start.x-end.x) /10, paint);
		float dist = (float) Math.sqrt((start.x-end.x)*(start.x-end.x) + (start.y-end.y) * (start.y-end.y));
		float alpha = (float) Math.asin(Math.abs(start.x - end.x) / dist);
		//bla
	}

}
