package org.giweet.sample.adventure;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.giweet.annotation.Step;

public class QuestStep {
	
	private String title;
	private Map<String, Person> persons = new HashMap<String, Person>();
	
	public QuestStep(String title, Hero hero) {
		this.title = title;
		persons.put(hero.getName(), hero);
		persons.put("the hero", hero);
	}
	
	@Step({
		"$0 encountered a $1.race", 
		"$0 encountered an $1.race", 
		"$0 encountered $1.name", 
		"$0 encountered a $1.race named $1.name", 
		"$0 encountered an $1.race named $1.name"})
	public void encounter(String subject, Person creature) {
		persons.put(creature.getRace(), creature);
		persons.put("the " + creature.getRace(), creature);
		if (creature.getName() != null) {
			creature.setName(creature.getName());
			persons.put(creature.getName(), creature);
		}
	}
	
	@Step("$0 killed $1")
	public void fight(String winner, String looser) {
		assertIsAlive(getPerson(winner));
		assertIsAlive(getPerson(looser));
		getPerson(looser).setAlive(false);
	}
	
	@Step("$0 was still alive")
	public void assertIsAlive(String person){
		assertIsAlive(getPerson(person));
	}
	
	@Step("$0 was dead")
	public void assertIsDead(String person){
		assertIsDead(getPerson(person));
	}

	@Step("$0 found $1 GP") 
	public void loot(String person, long gp){
		assertIsAlive(getPerson(person));
		getPerson(person).addGoldPieces(gp);
	}

	@Step("$0 went back home with $1 GP") 
	public void goBackHome(String person, long gp){
		assertIsAlive(getPerson(person));
		Assert.assertEquals("Unexpected gold pieces", gp, getPerson(person).getGoldPieces());
	}

	@Step("later on the legend was known as the quest of $0") 
	public void endQuest(String questName){
		Assert.assertEquals("Incorrect quest name", title, questName);
	}

	private void assertIsAlive(Person person) {
		Assert.assertTrue((person.getName() != null ? person.getName() : person.getRace()) + " died ealier", person.isAlive());
	}

	private void assertIsDead(Person person) {
		Assert.assertFalse((person.getName() != null ? person.getName() : person.getRace()) + " is still alive", person.isAlive());
	}

	private Person getPerson(String name) {
		Person person = persons.get(name);
		Assert.assertNotNull("No such character in the story: " + name, person);
		return person;
	}

}
