package net.ion.icrawler.selector;

/**
 * Object contains regex results.<br>
 * For multi group result extension.<br>
 */
class RegexResult {

	private String[] groups;

	public static final RegexResult EMPTY_RESULT = new RegexResult();

	public RegexResult() {

	}

	public RegexResult(String[] groups) {
		this.groups = groups;
	}

	public String get(int groupId) {
		if (groups == null) {
			return null;
		}
		return groups[groupId];
	}

}
