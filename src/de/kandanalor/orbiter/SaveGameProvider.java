package de.kandanalor.orbiter;

import java.util.HashMap;

import de.kandanalor.orbiter.game.Earth;
import de.kandanalor.orbiter.game.Merkur;
import de.kandanalor.orbiter.game.Moon;
import de.kandanalor.orbiter.game.World;

public class SaveGameProvider {
	private static HashMap<String, World> savegames = new HashMap<String, World> (); 
	private static World[] campaign = new World[1];
	private static int campaign_index = 0;
	public static final String QUICKSAVE = "quicksave";
	public static final String CAMPAIGN = "CAMPAIN_";
	static {
		World stdworld = new World();
		Moon moon = new Moon();
		Merkur merkur = new Merkur();
		Earth earth = new Earth();
		
		
        earth.setPos(0,0);
        earth.setMovement(0,0);
        
        merkur.setPos(-200,0);
        //merkur.setMovement(-50,0);

        
        moon.setPos(200,200);
        moon.setMovement(0,-50);
        
        
        stdworld.getObjects().add(earth);
        stdworld.getObjects().add(moon);
        stdworld.getObjects().add(merkur);
       
        
        stdworld.setName("Level 1");
        campaign[0] = stdworld;
	}
	public static World load(String name) {
		return savegames.get(name);
	}
	public static void save(String name, World world) {
		savegames.put(name, world);
	}
	public static World getCampaignLevel(int i) {
		return campaign[i];
	}
	public static World getCampaignLevel() {
		return campaign[campaign_index];
	}
	public static World nextCampaignLevel() {
		campaign_index++;
		return campaign[campaign_index];
	}
	public static void quicksave(World world) {
		save(QUICKSAVE, world);
	}
	public static World quickload(World world) {
		return load(QUICKSAVE);
	}
}
