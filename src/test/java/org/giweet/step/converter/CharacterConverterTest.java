package org.giweet.step.converter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;

import org.junit.Test;

public class CharacterConverterTest {
	
	@Test
	public void canGetSupportedClasses() {
		CharacterConverter underTest = new CharacterConverter();
		
		assertArrayEquals(new Class<?>[]{char.class, Character.class}, underTest.getSupportedClasses());
	}

	@Test
	public void canConvertValue() throws Exception {
		CharacterConverter underTest = new CharacterConverter();
		
		Character result = (Character) underTest.convert(char.class, new Annotation[0], "a");
		assertEquals(Character.valueOf('a'), result);

		result = (Character) underTest.convert(Character.class, new Annotation[0], "b");
		assertEquals(Character.valueOf('b'), result);
	}

	@Test(expected=CannotConvertException.class)
	public void cannotConvertValueWhenEmptyString() throws Exception {
		CharacterConverter underTest = new CharacterConverter();
		
		underTest.convert(char.class, new Annotation[0], "");
	}

	@Test(expected=CannotConvertException.class)
	public void cannotConvertValueWhenTargetClassIsNotCharacter() throws Exception {
		CharacterConverter underTest = new CharacterConverter();
		
		underTest.convert(int.class, new Annotation[0], "1");
	}

	@Test(expected=CannotConvertException.class)
	public void cannotConvertValueWhenStringContainsMoreThanOneCharacter() throws Exception {
		CharacterConverter underTest = new CharacterConverter();
		
		underTest.convert(char.class, new Annotation[0], "ab");
	}
}
