package org.giweet;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Test;

public class JavaBeanUtilsTest {
	
	public static class JavaBean {

		public String getStringProperty() {
			return null;
		}

		public void setStringProperty(String stringProperty) {
		}

		public boolean isBooleanProperty() {
			return false;
		}

		public boolean getOtherBooleanProperty() {
			return false;
		}

		public Boolean isWrapperBooleanProperty() {
			return false;
		}

		public void setBooleanProperty(boolean booleanProperty) {
		}
		
		public void setInvalidProperty(String arg1, String arg2) {
		}

		public void getInvalidProperty() {
		}

		@SuppressWarnings("unused")
		private String getPrivateProperty() {
			return null;
		}
	}

	@Test
	public void canGetReaderMethod() throws Exception {
		Method readerMethod = JavaBeanUtils.getReaderMethod(JavaBean.class, "stringProperty");
		assertEquals("getStringProperty", readerMethod.getName());
		
		readerMethod = JavaBeanUtils.getReaderMethod(JavaBean.class, "booleanProperty");
		assertEquals("isBooleanProperty", readerMethod.getName());

		readerMethod = JavaBeanUtils.getReaderMethod(JavaBean.class, "otherBooleanProperty");
		assertEquals("getOtherBooleanProperty", readerMethod.getName());

		readerMethod = JavaBeanUtils.getReaderMethod(JavaBean.class, "wrapperBooleanProperty");
		assertEquals("isWrapperBooleanProperty", readerMethod.getName());
	}

	@Test
	public void canGetWriterMethod() throws Exception {
		Method readerMethod = JavaBeanUtils.getWriterMethod(JavaBean.class, "stringProperty");
		assertEquals("setStringProperty", readerMethod.getName());
		
		readerMethod = JavaBeanUtils.getWriterMethod(JavaBean.class, "booleanProperty");
		assertEquals("setBooleanProperty", readerMethod.getName());
	}

	@Test(expected=NoSuchMethodException.class)
	public void cannotGetUnknownReaderMethod() throws Exception {
		JavaBeanUtils.getReaderMethod(JavaBean.class, "property");
	}

	@Test(expected=NoSuchMethodException.class)
	public void cannotGetUnknownWriterMethod() throws Exception {
		JavaBeanUtils.getWriterMethod(JavaBean.class, "property");
	}

	@Test(expected=NoSuchMethodException.class)
	public void cannotGetInvalidReaderMethod() throws Exception {
		JavaBeanUtils.getReaderMethod(JavaBean.class, "invalidProperty");
	}

	@Test(expected=NoSuchMethodException.class)
	public void cannotGetInvalidWriterMethod() throws Exception {
		JavaBeanUtils.getWriterMethod(JavaBean.class, "invalidProperty");
	}

	@Test(expected=NoSuchMethodException.class)
	public void cannotGetPrivateWriterMethod() throws Exception {
		JavaBeanUtils.getWriterMethod(JavaBean.class, "privateProperty");
	}
}
