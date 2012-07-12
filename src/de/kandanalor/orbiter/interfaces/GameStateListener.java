package de.kandanalor.orbiter.interfaces;

import de.kandanalor.orbiter.game.World;

public interface GameStateListener {
	public void onGamePaused();
	public void onGamePhysikUpdate(World world);
	public void onGameResumed();
	public void onGameOver();
}
