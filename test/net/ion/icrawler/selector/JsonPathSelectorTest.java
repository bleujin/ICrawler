package net.ion.icrawler.selector;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;


public class JsonPathSelectorTest extends TestCase{

	private String text = "{ \"store\": {\n" + 
	"    \"book\": [ \n" + 
	"      { \"category\": \"reference\",\n" + 
	"        \"author\": \"Nigel Rees\",\n" + 
	"        \"title\": \"Sayings of the Century\",\n" + 
	"        \"price\": 8.95\n" + "      },\n" + 
	"      { \"category\": \"fiction\",\n"+ 
	"        \"author\": \"Evelyn Waugh\",\n" + 
	"        \"title\": \"Sword of Honour\",\n" + 
	"        \"price\": 12.99,\n" + 
	"        \"isbn\": \"0-553-21311-3\"\n" + 
	"      }\n" + 
	"    ],\n" + 
	"    \"bicycle\": {\n" + 
	"      \"color\": \"red\",\n" + 
	"      \"price\": 19.95\n" + 
	"    }\n" + "  }\n" + "}";

	@Test
	public void testJsonPath() {
		JsonPathSelector jsonPathSelector = new JsonPathSelector("$.store.book[*].author");
		String select = jsonPathSelector.select(text);
		List<String> list = jsonPathSelector.selectList(text);
		assertEquals("Nigel Rees",select);
		assertEquals(true, list.contains("Nigel Rees"));
		assertEquals(true, list.contains("Evelyn Waugh"));
		jsonPathSelector = new JsonPathSelector("$.store.book[?(@.category == 'reference')]");
		list = jsonPathSelector.selectList(text);
		select = jsonPathSelector.select(text);
		assertEquals("{\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"category\":\"reference\",\"price\":8.95}" ,select);
		assertEquals(true, list.contains("{\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"category\":\"reference\",\"price\":8.95}"));
	}
}
