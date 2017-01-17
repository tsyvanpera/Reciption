package com.reciption;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
		StringBuilder builder = new StringBuilder();

		Pattern pattern = Pattern.compile("Valmistusaineet");
		SpringApplication.run(ReciptionApplication.class, args);

		Document doc = Jsoup
				.connect("https://www.k-ruoka.fi/reseptit/ranskalainen-sipulikeitto")
				.get();

		//Elements elements = doc.getElementsByAttributeValue("class", "nimet");

		for (Element e : doc.children()) {
			//System.out.println(e);
			System.out.println(jsonifyElement(e, builder));
		}
		
        InputStream stream = new ByteArrayInputStream(builder.toString().getBytes("UTF-8"));
		ReciptionParser parser = new ReciptionParser(stream);
		
		parser.recipe();
	}

	public static StringBuilder jsonifyElement(Node node, StringBuilder builder) {
		if (node instanceof TextNode) {
			TextNode text = (TextNode) node;
			builder.append("\"" + text.text() + "\"\n");
		}
		if (node.childNodes().size() > 0) {
			for (Node e : node.childNodes()) {
				jsonifyElement(e, builder);
			}
		}
		return builder;
	}
}
