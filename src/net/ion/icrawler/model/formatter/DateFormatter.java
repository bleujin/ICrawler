package net.ion.icrawler.model.formatter;

import java.util.Date;

import net.ion.framework.util.DateUtil;

public class DateFormatter implements ObjectFormatter<Date> {

	public static final String[] DEFAULT_PATTERN = new String[] { "yyyy-MM-dd HH:mm" };
	private String[] datePatterns = DEFAULT_PATTERN;

	@Override
	public Date format(String raw) throws Exception {
		return DateUtil.stringToDate(raw, datePatterns[0]);
	}

	@Override
	public Class<Date> clazz() {
		return Date.class;
	}

	@Override
	public void initParam(String[] extra) {
		if (extra != null && !(extra.length == 1 && extra[0].length() == 0)) {
			datePatterns = extra;
		}
	}
}
