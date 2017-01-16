package com.reciption;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReciptionApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(ReciptionApplication.class, args);
		Document doc = Jsoup.connect("http://www.kodinkuvalehti.fi/artikkeli/ruoka/reseptit/moskovanpata").get();

		System.out.println(doc);
	}
}
