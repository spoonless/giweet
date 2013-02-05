package org.giweet.sample.adventure;

import java.util.Collection;
import java.util.Locale;

import org.giweet.MethodStepDeclaration;
import org.giweet.MethodStepInvoker;
import org.giweet.ParamStepConverter;
import org.giweet.StepObjectHandler;
import org.giweet.converter.BooleanConverter;
import org.giweet.converter.CalendarConverter;
import org.giweet.converter.CharacterConverter;
import org.giweet.converter.Converter;
import org.giweet.converter.ConverterComposite;
import org.giweet.converter.DateConverter;
import org.giweet.converter.EnumConverter;
import org.giweet.converter.NumberConverter;
import org.giweet.converter.SimpleStringConverter;
import org.giweet.step.StepInstance;
import org.giweet.step.StepType;
import org.giweet.step.tree.SearchResult;
import org.giweet.step.tree.StepDeclarationTree;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("can be reactivated when StepDeclarationTree will be reimplemented")
public class AdventureRunner {

	@Test
	public void testAdventure() throws Exception {
		AdventureStep adventureStep = new AdventureStep();
		
		String[] scenario = {
				"Once upon a time, a hero named Emile",
				"the hero was a powerful warrior",
				"the hero had a sword",
				"the hero rode to the magical castle", 
				"Emile encountered a group of giant rats", 
				"Emile killed the group of giant rats", 
				"Emile was still alive", 
				"the group of giant rats was dead", 
				"Emile found 50 GP", 
				"Emile encountered a red dragon named Fafner", 
				"Fafner was still alive",
				"the hero killed Fafner",
				"Fafner was dead", 
				"Emile found 1000 GP", 
				"Emile was still alive", 
				"the hero went back home with 1050 GP", 
				"later on, the legend was known as the quest of the magical castle!", 
		};
		
		runScenario(adventureStep, scenario);
	}

	private void runScenario(Object step, String...scenario) throws Exception {
		MethodStepInvoker methodStepInvoker = createMethodStepInvoker();
		StepObjectHandler stepObjectHandler = new StepObjectHandler(step);
		
		stepObjectHandler.check();

		Collection<MethodStepDeclaration> methodStepDeclarations = stepObjectHandler.getMethodStepDeclarations();
		StepDeclarationTree<MethodStepDeclaration> tree = new StepDeclarationTree<MethodStepDeclaration>(methodStepDeclarations);
		
		for (String scenarioStep : scenario) {
			SearchResult<MethodStepDeclaration> searchResult = tree.search(new StepInstance(StepType.GIVEN, scenarioStep));
			Object result = methodStepInvoker.invoke(searchResult.getStepDeclaration(), searchResult.getStepTokenValues());
			if (result != null) {
				stepObjectHandler = new StepObjectHandler(result);
				stepObjectHandler.check();
				methodStepDeclarations = stepObjectHandler.getMethodStepDeclarations();
				if (!methodStepDeclarations.isEmpty()) {
					tree = new StepDeclarationTree<MethodStepDeclaration>(methodStepDeclarations);
				}
			}
		}
	}

	public MethodStepInvoker createMethodStepInvoker() throws Exception {
		Converter converter = new ConverterComposite(new BooleanConverter("true"), new CalendarConverter(Locale.US, "MM/dd/yyyy"), new CharacterConverter(), new DateConverter(Locale.US, "MM/dd/yyyy"), new EnumConverter(), new NumberConverter(Locale.US), new SimpleStringConverter());
		ParamStepConverter paramStepConverter = new ParamStepConverter(converter, ",");
		return new MethodStepInvoker(paramStepConverter);
	}
}
