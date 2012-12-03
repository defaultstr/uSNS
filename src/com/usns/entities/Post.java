/* vi:set noet: */
package com.usns.entities;

import java.util.Date;

public class Post {
	public String user;
	public Date date;
	public String text;
	public String source;
	public String sourceId;

	@Override
	public String toString() {
		return "Post[user(" + user + "), date(" + date.toString() + "), text("
				+ text + "), source(" + source + "), sourceId(" + sourceId
				+ ")]";
	}
}
