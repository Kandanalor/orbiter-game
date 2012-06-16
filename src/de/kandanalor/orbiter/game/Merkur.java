package de.kandanalor.orbiter.game;

import android.content.Context;
import android.graphics.BitmapFactory;
import de.kandanalor.orbiter.R;

public class Merkur extends Planet {
	private static final String TAG = "Merkur";
	
	public Merkur() {
		super();
		setRadius(40);
		setMass(40);
		setBitmapResource(R.drawable.merkur);
		setName("Merkur");
	}
}
