package org.giweet.sample.adventure;

public class Person {
	
	private String name;
	private String race;
	private Weapon weapon;
	private long goldPieces;
	private boolean isAlive = true;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}
	
	public void addGoldPieces(long gp) {
		this.goldPieces += gp;
	}

	public long getGoldPieces() {
		return goldPieces;
	}

	public void setGoldPieces(long goldPieces) {
		this.goldPieces = goldPieces;
	}
}
