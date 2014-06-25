package net.ion.icrawler.model;

import net.ion.icrawler.model.annotation.ExtractBy;

/**

 */
public class BaseRepo {

	@ExtractBy("//ul[@class='pagehead-actions']/li[1]//a[@class='social-count js-social-count']/text()")
	protected int star;
}
