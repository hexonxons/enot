package app.tascact.manual.utils;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;

import android.util.Log;
public class XMLUtils {
	/**
	 * Applies xpath expression to the node. 
	 * @param node node to apply expression to.
	 * @param expr xpath expression to apply.
	 * @param rType specifies return type.
	 * @return returns null on failure.
	 */
	public static Object evalXpathExpr(Node node, String expr, QName rType) {
		try {
			XPathFactory fac = XPathFactory.newInstance();
			XPath xpath = fac.newXPath();		
			XPathExpression pathexp = xpath.compile(expr);
			return pathexp.evaluate(node, rType);
		}
		catch (XPathExpressionException e) {
			e.printStackTrace();
			Log.e("XML", e.getMessage());
			return null;
		}
	}
}
