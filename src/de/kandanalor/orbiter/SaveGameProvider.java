package de.kandanalor.orbiter;

import java.util.HashMap;

import de.kandanalor.orbiter.game.Earth;
import de.kandanalor.orbiter.game.Merkur;
import de.kandanalor.orbiter.game.Moon;
import de.kandanalor.orbiter.game.World;

public class SaveGameProvider {
	public static HashMap<String, World> savegames = new HashMap<String, World> (); 
	public static final String QUICKSAVE = "quicksave";
	static {
		World stdworld = new World();
		Moon moon = new Moon();
		Merkur merkur = new Merkur();
		Earth earth = new Earth();
		
        earth.setPos(0,-200);
        earth.setMovement(0,0);
        
        merkur.setPos(0,0);
        merkur.setMovement(-50,0);

        
        moon.setPos(200,200);
        moon.setMovement(0,-50);
        
        stdworld.getObjects().add(moon);
        stdworld.getObjects().add(merkur);
        stdworld.getObjects().add(earth);
        savegames.put(QUICKSAVE, stdworld);
	}
}
