package net.ion.icrawler.utils;

import junit.framework.TestCase;

import org.junit.Assert;

public class UrlUtilsTest extends TestCase{

	public void testFixRelativeUrl() {
		String absoluteUrl = UrlUtils.canonicalizeUrl("aa", "http://www.dianping.com/sh/ss/com");
		assertEquals(absoluteUrl, "http://www.dianping.com/sh/ss/aa");

		absoluteUrl = UrlUtils.canonicalizeUrl("../aa", "http://www.dianping.com/sh/ss/com");
		assertEquals(absoluteUrl, "http://www.dianping.com/sh/aa");

		absoluteUrl = UrlUtils.canonicalizeUrl("..aa", "http://www.dianping.com/sh/ss/com");
		assertEquals(absoluteUrl, "http://www.dianping.com/sh/ss/..aa");

		absoluteUrl = UrlUtils.canonicalizeUrl("../../aa", "http://www.dianping.com/sh/ss/com/");
		assertEquals(absoluteUrl, "http://www.dianping.com/sh/aa");

		absoluteUrl = UrlUtils.canonicalizeUrl("../../aa", "http://www.dianping.com/sh/ss/com");
		assertEquals(absoluteUrl, "http://www.dianping.com/aa");
	}

	public void testFixAllRelativeHrefs() {
		String originHtml = "<a href=\"/start\">";
		String replacedHtml = UrlUtils.fixAllRelativeHrefs(originHtml, "http://www.dianping.com/");
		assertEquals(replacedHtml, "<a href=\"http://www.dianping.com/start\">");

		originHtml = "<a href=\"/start a\">";
		replacedHtml = UrlUtils.fixAllRelativeHrefs(originHtml, "http://www.dianping.com/");
		assertEquals(replacedHtml, "<a href=\"http://www.dianping.com/start%20a\">");

		originHtml = "<a href='/start a'>";
		replacedHtml = UrlUtils.fixAllRelativeHrefs(originHtml, "http://www.dianping.com/");
		assertEquals(replacedHtml, "<a href=\"http://www.dianping.com/start%20a\">");

		originHtml = "<a href=/start tag>";
		replacedHtml = UrlUtils.fixAllRelativeHrefs(originHtml, "http://www.dianping.com/");
		assertEquals(replacedHtml, "<a href=\"http://www.dianping.com/start\" tag>");
	}

	public void testGetDomain() {
		String url = "http://www.dianping.com/aa/";
		Assert.assertEquals("www.dianping.com", UrlUtils.getDomain(url));
		url = "www.dianping.com/aa/";
		Assert.assertEquals("www.dianping.com", UrlUtils.getDomain(url));
		url = "http://www.dianping.com";
		Assert.assertEquals("www.dianping.com", UrlUtils.getDomain(url));
	}

}
