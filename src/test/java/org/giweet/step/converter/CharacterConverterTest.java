package org.giweet.step.converter;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;

import org.giweet.step.StepTokenizer;
import org.junit.Test;

public class CharacterConverterTest {
	
	private StepTokenizer stepTokenizer = new StepTokenizer(false, true);

	@Test
	public void canConvert() {
		CharacterConverter underTest = new CharacterConverter();
		
		assertTrue(underTest.canConvert(char.class));
		assertTrue(underTest.canConvert(Character.class));
	}

	@Test
	public void canConvertValue() throws Exception {
		CharacterConverter underTest = new CharacterConverter();
		
		Character result = (Character) underTest.convert(char.class, new Annotation[0], stepTokenizer.tokenize("a"));
		assertEquals(Character.valueOf('a'), result);

		result = (Character) underTest.convert(Character.class, new Annotation[0], stepTokenizer.tokenize("b"));
		assertEquals(Character.valueOf('b'), result);
	}

	@Test
	public void canConvertArrayValue() throws Exception {
		CharacterConverter underTest = new CharacterConverter();
		
		char[] result = (char[]) underTest.convert(char[].class, new Annotation[0], stepTokenizer.tokenize("abc"));
		assertArrayEquals(new String("abc").toCharArray(), result);
	}

	@Test
	public void canConvertArrayValueOfWrapperClass() throws Exception {
		CharacterConverter underTest = new CharacterConverter();
		
		Character[] result = (Character[]) underTest.convert(Character[].class, new Annotation[0], stepTokenizer.tokenize("abc"));
		assertArrayEquals(new Character[]{'a', 'b', 'c'}, result);

		result = (Character[]) underTest.convert(Character[].class, new Annotation[0], stepTokenizer.tokenize(""));
		assertArrayEquals(new Character[0], result);
	}

	@Test(expected=CannotConvertException.class)
	public void cannotConvertValueWhenEmptyString() throws Exception {
		CharacterConverter underTest = new CharacterConverter();
		
		underTest.convert(char.class, new Annotation[0], stepTokenizer.tokenize(""));
	}

	@Test(expected=CannotConvertException.class)
	public void cannotConvertValueWhenStringContainsMoreThanOneCharacter() throws Exception {
		CharacterConverter underTest = new CharacterConverter();
		
		underTest.convert(char.class, new Annotation[0], stepTokenizer.tokenize("ab"));
	}
}
