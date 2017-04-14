package edu.scripps.yates.utilities.jaxb.xpathquery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class that perform a query over an object created with JAXB from an XML.<br>
 * It will return a list of values specified by two XPaths, one that indicates
 * the element (and optionally perform a value check) and the other which
 * specifies the final element to return
 * 
 * @author Salva
 *
 */
public class JAXBXPathQuery {
	/**
	 * Example, using the Entry object from uniprot xml schema: String xpath =
	 * "$id"; String filterXPath = "dbReference$type=GO";
	 * 
	 * @param jaxbObject
	 *            the jaxb object
	 * @param xpath
	 *            the main XPath string. It can contain an attribute
	 *            (dbReference$type). If it contains an attribute (separated by
	 *            $), a condition in the value of the condition can be written
	 *            as "dbReference$type=GO"
	 * @param subXpath
	 *            an XPath applied to the deepest element in the queryXPath.
	 *            This is the value that will be returned.
	 * @return
	 */
	public static List<String> query(Object jaxbObject, String xpath, String subXpath) {
		XPathParser xpathParserFilter = new XPathParser(xpath);
		XPathParser xpathParser = new XPathParser(subXpath);

		Set<Object> set = new HashSet<Object>();
		set.add(jaxbObject);
		try {
			List<Object> explore = explore(xpathParserFilter, xpathParser, set, set);
			List<String> ret = new ArrayList<String>();
			for (Object object : explore) {
				ret.add(object.toString());
			}
			return ret;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	private static List<Object> explore(XPathParser xPathParserFilter, XPathParser xPathParser,
			Collection<Object> originalObjects, Collection<Object> objs)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, CloneNotSupportedException {
		List<Object> list = new ArrayList<Object>();
		boolean hasNext = xPathParserFilter.hasNext();
		String elementName = null;
		if (hasNext) {
			elementName = xPathParserFilter.next();
		}
		String attribute = xPathParserFilter.getAttribute();
		String filterValue = xPathParserFilter.getFilterValue();
		for (Object object : objs) {
			if (hasNext) {
				Method method = object.getClass().getMethod("get" + capitalizeFirstLetter(elementName), null);
				Object returnedObj = method.invoke(object, null);
				if (returnedObj != null && returnedObj instanceof Collection) {
					Collection<Object> collection = (Collection<Object>) returnedObj;
					list.addAll(explore(xPathParserFilter, xPathParser, originalObjects, collection));
				} else {
					Set<Object> set = new HashSet<Object>();
					set.add(returnedObj);
					list.addAll(explore(xPathParserFilter, xPathParser, originalObjects, set));
				}
			} else {
				if (attribute != null) {
					Method method = object.getClass().getMethod("get" + capitalizeFirstLetter(attribute), null);
					Object returnedObj = method.invoke(object, null);
					if (returnedObj != null) {
						if (filterValue != null) {
							if (filterValue.equals(returnedObj.toString())) {
								Set<Object> set = new HashSet<Object>();
								set.add(object);
								list.addAll(explore((XPathParser) xPathParser.clone(), null, null, set));
							}
						} else {
							list.add(returnedObj);
						}
					}
				} else {
					// getValue
					Method method = object.getClass().getMethod("getValue", null);
					Object returnedObj = method.invoke(object, null);
					if (returnedObj != null) {
						if (filterValue != null) {
							if (filterValue.equals(returnedObj)) {
								list.addAll(explore(xPathParser, null, null, originalObjects));
							}
						} else {
							list.add(returnedObj);
						}
					}
				}
			}
		}
		return list;
	}

	private static String capitalizeFirstLetter(String string) {
		String string2 = Character.toUpperCase(string.charAt(0)) + string.substring(1);
		return string2;
	}
}
