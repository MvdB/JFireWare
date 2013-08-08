package de.edvdb.ffw.beans;

import org.apache.log4j.Logger;

public class Field {
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(Field.class);
	private String id;
	private String findPattern;
	private Integer index;
	private boolean extendRight;
	private boolean extendDown;
	private boolean unique;
	private String startPattern;
	private String endPattern;
	private Integer lineOffset;
	private boolean isSet;
	private boolean isAlphaOnly;
	private boolean excludeStartpattern;

	private Object value;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public boolean isExtendRight() {
		return extendRight;
	}

	public void setExtendRight(boolean extendRight) {
		this.extendRight = extendRight;
	}

	public String getEndPattern() {
		return endPattern;
	}

	public void setEndPattern(String endPattern) {
		this.endPattern = endPattern;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Field(String id, String findPattern, String index,
			String extendRight, String extendDown, String startPattern,
			String endPattern, String lineOffset, String unique,
			String isAlphaOnly, String excludeStartpattern) {
		super();
		this.id = id;
		this.findPattern = findPattern;
		this.index = Integer.valueOf(index);
		this.extendRight = Boolean.valueOf(extendRight);
		this.extendDown = Boolean.valueOf(extendDown);
		this.startPattern = startPattern;
		this.endPattern = endPattern;
		this.lineOffset = Integer.valueOf(lineOffset);
		this.unique = Boolean.valueOf(unique);
		this.isAlphaOnly = Boolean.valueOf(isAlphaOnly);
		this.excludeStartpattern = Boolean.valueOf(excludeStartpattern);
	}

	public void setFindPattern(String findPattern) {
		this.findPattern = findPattern;
	}

	public String getFindPattern() {
		return findPattern;
	}

	public void setLineOffset(Integer lineOffset) {
		this.lineOffset = lineOffset;
	}

	public Integer getLineOffset() {
		return lineOffset;
	}

	public void setSet(boolean isSet) {
		this.isSet = isSet;
	}

	public boolean isSet() {
		return isSet;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setExtendDown(boolean extendDown) {
		this.extendDown = extendDown;
	}

	public boolean isExtendDown() {
		return extendDown;
	}

	public void setStartPattern(String startPattern) {
		this.startPattern = startPattern;
	}

	public String getStartPattern() {
		return startPattern;
	}

	@Override
	public String toString() {
		return id;
	}

	public void setAlphaOnly(boolean isAlphaOnly) {
		this.isAlphaOnly = isAlphaOnly;
	}

	public boolean isAlphaOnly() {
		return isAlphaOnly;
	}

	public boolean isExcludeStartpattern() {
		return excludeStartpattern;
	}

	public void setExcludeStartpattern(boolean excludeStartpattern) {
		this.excludeStartpattern = excludeStartpattern;
	}

}