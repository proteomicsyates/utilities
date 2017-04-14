package edu.scripps.yates.utilities.jaxb.xpathquery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XPathParser implements Iterator<String>, Cloneable {
	private final List<String> elements = new ArrayList<String>();
	private String attribute;
	private String filterValue;
	private String currentElement;
	private Iterator<String> iterator;
	private final String xpath;

	public XPathParser(String xpath) {
		this.xpath = xpath;
		if (xpath.contains("=")) {
			String[] split = xpath.split("=");
			parse(split[0]);
			this.filterValue = split[1];
		} else {
			parse(xpath);
		}
	}

	private void parse(String xpath) {

		List<String> elements2 = new ArrayList<String>();
		if (xpath.contains("/")) {
			String[] split = xpath.split("/");
			for (String element : split) {
				elements2.add(element.trim());
			}
		} else {
			elements2.add(xpath.trim());
		}

		for (String element : elements2) {
			if (element.contains("$")) {
				String[] split = element.split("\\$");
				if (!"".equals(split[0].trim())) {
					this.elements.add(split[0].trim());
				}
				if (!"".equals(split[1].trim())) {
					this.attribute = split[1].trim();
				}
			} else {
				if (!"".equals(element.trim())) {
					this.elements.add(element.trim());
				}
			}
		}
		this.iterator = elements.iterator();
	}

	public List<String> getElements() {
		return elements;
	}

	public String getAttribute() {
		return attribute;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public String next() {
		String next = iterator.next();
		this.currentElement = next;
		return next;
	}

	public String getCurrentElement() {
		return currentElement;
	}

	public String getFilterValue() {
		return filterValue;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		XPathParser ret = new XPathParser(xpath);
		return ret;
	}
}
