package com.reciption;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ReciptionApplication {
	private static int elementcount = 0;

	public static void main(String[] args) throws IOException, ParseException {
		IndentedStringBuilder builder = new IndentedStringBuilder(new StringBuilder());

		Pattern pattern = Pattern.compile("Valmistusaineet");
		SpringApplication.run(ReciptionApplication.class, args);

		Document doc = Jsoup
				.connect("https://www.k-ruoka.fi/reseptit/ranskalainen-sipulikeitto")
				.get();

		//Elements elements = doc.getElementsByAttributeValue("class", "nimet");

		for (Element e : doc.children()) {
			System.out.println(e);
			System.out.println(preTokenizeElementTree(e, builder, 0));
		}
		
        InputStream stream = new ByteArrayInputStream(builder.toString().getBytes("UTF-8"));
		ReciptionParser parser = new ReciptionParser(stream);
		
		parser.recipe();
	}

	public static IndentedStringBuilder preTokenizeElementTree(Node node, IndentedStringBuilder builder, int indent) {
		boolean isListNode = isListNode(node) && node instanceof Element;
		if (shouldSkipNode(node)) {
			return builder;
		}
		identifyAndQuoteTextElement(node, builder);
		identifyAndTokenizeImage(node, builder);
		if (identifyAndTokenizeHeading(node, builder)) {
			return builder;
		};
		
		if (isListNode) {
			builder.setIndent(++indent);
			builder.append("\"LIST_BEGIN: (" + ((Element)node).tagName() + " - " + node.attr("class") + ")\"\n");
		}
		if (node.childNodes().size() > 0) {
			for (Node e : node.childNodes()) {
				preTokenizeElementTree(e, builder, indent);
			}
		}
		if (isListNode) {
			builder.append("\"LIST_END: (" + ((Element)node).tagName() + " - " + node.attr("class") + ")\"\n");
			builder.setIndent(--indent);
		}
		return builder;
	}
	
	private static void identifyAndTokenizeImage(Node node, IndentedStringBuilder builder) {
		if (node instanceof Element) {
			Element element = (Element) node;
			if (element.tagName().equals("img")) {
				builder.append("\"IMAGE: " + element.attr("src") + " - " + element.attr("alt") + "\"\n");
			}
		}
	}

	private static boolean identifyAndTokenizeHeading(Node node, IndentedStringBuilder builder) {
		if (node instanceof Element) {
			Element element = (Element) node;
			if (element.tagName().matches("h[1,2,3]")) {
				builder.append("\"HEADING(" + element.tagName() + "): " + element.text() + "\"\n");
				return true;
			}
		}
		return false;
	}
	
	private static boolean isListNode(Node node) {
		if (node.childNodeSize() < 2) {
			return false;
		}
		Map<String, Node> categorizedListNodes = new HashMap<String, Node>();
		for (Node n: node.childNodes()) {
			if (n instanceof Element) {
				categorizedListNodes.put(((Element) n).tagName(), n);
			}
		}
		return categorizedListNodes.size() > 1;
	}

	private static void identifyAndQuoteTextElement(Node node, IndentedStringBuilder builder) {
		if (node instanceof TextNode) {
			TextNode text = (TextNode) node;
			builder.append("\"" + text.text() + "\"\n");
		}

	}
	private static boolean shouldSkipNode(Node node) {
		if (node instanceof Element) {
			if (((Element) node).tagName().equals("head")) {
				return true;
			}
		}
		return false;
	}
}
