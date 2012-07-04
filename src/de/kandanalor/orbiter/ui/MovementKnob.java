package de.kandanalor.orbiter.ui;

import de.kandanalor.orbiter.game.GameObject;
import android.content.Context;
import android.graphics.PointF;

public class MovementKnob extends VectorKnob {

	public MovementKnob(GameObject origin, Context context) {
		super(origin, context);
	}
	@Override
	public PointF getValue() {
		return origin.getMovement();
	}


	@Override
	public void setValue(PointF mov) {
		origin.setMovement(mov.x, mov.y);
	}

}
