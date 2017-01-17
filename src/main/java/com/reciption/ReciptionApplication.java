package com.reciption;

import java.io.IOException;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sun.mail.handlers.text_html;

@SpringBootApplication
public class ReciptionApplication {
	private static int elementcount = 0;
	
	public static void main(String[] args) throws IOException {
		StringBuilder builder = new StringBuilder();
		
		Pattern pattern = Pattern.compile("Valmistusaineet");
		SpringApplication.run(ReciptionApplication.class, args);
		
		Document doc = Jsoup.connect("http://nimisto.birdlife.fi:3000/haku?hakusanat=parus&nimikentta=kaikki&matching=osa").get();

		Elements elements = doc.getElementsByAttributeValue("class", "nimet");
		
		for (Element e: elements) {
			System.out.println(e);
			System.out.println(jsonifyElement(e, builder));
		}
	}
	
	public static StringBuilder jsonifyElement(Node node, StringBuilder builder) {
		builder.append("{");
		if (node instanceof TextNode) {
			TextNode text = (TextNode) node;
			builder.append("\"text_" + (elementcount++) + "\": \"");
			builder.append(text.text());
			builder.append("\",");
		}
		if (node.childNodes().size() > 0) {
			if (node instanceof Element) {
				Element element = (Element) node;
				builder.append("\"" + element.tagName() + "_" + (elementcount++) + "\": [");
				for (Node e: node.childNodes()) {
					jsonifyElement(e, builder);
				}
				builder.append("],");
			}
		}
		builder.append("},");
		return builder;
	}
}
