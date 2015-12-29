package mmb.util;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.regex.Pattern;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ReaderWml {

	static Transformer transformer = null;
	static Templates transTemplate = null;
	static DocumentBuilder builder = null;
	static byte[] nullDtd = "<?xml version='1.0' encoding='UTF-8'?>".getBytes();


	public static void init() {
		if(transformer != null)
			return;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		try {
			builder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		builder.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(java.lang.String publicId,
					java.lang.String systemId) throws SAXException,
					java.io.IOException {
				if (publicId.equals("-//WAPFORUM//DTD WML 1.1//EN"))
					return new InputSource(new ByteArrayInputStream(nullDtd));
				else
					return null;
			}
		});

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		try {
			transTemplate = transformerFactory.newTemplates(new StreamSource(
					ReaderWml.class.getResourceAsStream("/source.xsl")));

			transformer = transTemplate.newTransformer();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	protected static Pattern removePattern = Pattern.compile("[\r\n]+[ \t]*");
	
	public static String convert(String content) {
		init();
		try {
			Document doc = builder.parse(new ByteArrayInputStream(content.getBytes("utf-8")));
			StringWriter sw = new StringWriter(1024);
			synchronized(ReaderWml.class) {
				transformer.transform(new DOMSource(doc), new StreamResult(sw));
			}
			content = removePattern.matcher(sw.toString()).replaceAll("");
		} catch (Exception e) {
		}
		return  content;
	}
}
