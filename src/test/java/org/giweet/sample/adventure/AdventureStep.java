package org.giweet.sample.adventure;

import junit.framework.Assert;

import org.giweet.annotation.Step;

public class AdventureStep {

	private Hero hero = new Hero();
	
	public Hero getHero() {
		return hero;
	}

	@Step("once upon a time a hero named $hero.name")
	public void startAdventure() {
	}
	
	@Step({
		"the hero had a $hero.weapon", 
		"the hero had an $hero.weapon", 
		"the hero was a $hero.race", 
		"the hero was an $hero.race"})
	public void describeHero() {
		Assert.assertNotNull("No hero defined for this adventure", hero.getName());
	}

	@Step("the hero rode to $0")
	public QuestStep startQuest(String location) {
		return new QuestStep(location, hero);
	}
}
