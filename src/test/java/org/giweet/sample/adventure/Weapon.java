package org.giweet.sample.adventure;

public enum Weapon {
	
	SWORD, AXE, BOW, LOLLIPOP;

	public String toString() {
		switch (this) {
		case SWORD:
			return "sword";
		case AXE:
			return "axe";
		case BOW:
			return "bow";
		}
		return this.name();
	}
}
