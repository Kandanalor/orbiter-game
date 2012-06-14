package de.kandanalor.orbiter.game;

import android.content.Context;
import android.graphics.BitmapFactory;
import de.kandanalor.orbiter.R;

public class Merkur extends Planet {
	private static final String TAG = "Merkur";
	
	public Merkur(Context context) {
		super();
		setRadius(40);
		setMass(40);
		setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.merkur));
		setName("Merkur");
	}
}
