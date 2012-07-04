package de.kandanalor.orbiter.exceptions;

import de.kandanalor.orbiter.game.GameObject;

public class ObjectCollisionException extends Exception {
	GameObject[] objects = null;
	
	public ObjectCollisionException(GameObject a, GameObject b){
		objects = new GameObject[]{a,b};
	}
	public GameObject[] getObjects() {
		return objects;
	}
}
