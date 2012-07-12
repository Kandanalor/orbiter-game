package de.kandanalor.orbiter.physics;

import android.graphics.PointF;

public class Force extends PointF {

	public Force(float x, float y) {
		super(x,y);
	}
	public Force invert() {
		return new Force(-x, -y);
	}
	@Override
	public String toString() {
		return "Force("+x+","+y+")";
	}
	public PointF getAcceleration(int mass) {
		return new PointF(x/mass, y/mass);
	}
}
