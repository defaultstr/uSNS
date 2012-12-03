/* vi:set noet: */

package com.usns;

import java.net.UnknownHostException;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class DB {
	private static Mongo mongo = null;
	private static com.mongodb.DB db = null;

	public static DBCollection getCollection(String name) {
		if (mongo == null) {
			try {
				mongo = new Mongo();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			db = mongo.getDB("usns");
		}
		return db.getCollection(name);
	}
}
