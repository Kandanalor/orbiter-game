package de.kandanalor.orbiter.interfaces;

public interface GameStateListener {
	public void onGamePaused();
	public void onGameResumed();
	public void onGameOver();
}
