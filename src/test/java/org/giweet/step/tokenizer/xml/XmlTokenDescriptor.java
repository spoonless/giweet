package org.giweet.step.tokenizer.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class XmlTokenDescriptor {
	
	private String type;

	private String value;

	@XmlAttribute
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public boolean isSeparator() {
		return "separator".equals(type);
	}
	
	public boolean isDynamic() {
		return "dynamic".equals(type);
	}

	@XmlValue
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
