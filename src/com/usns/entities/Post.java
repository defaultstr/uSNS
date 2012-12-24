/* vi:set noet: */
package com.usns.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import org.bson.types.ObjectId;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Post {
	public String owner;
	public String uid;
	public String user;
	public Date date;
	public String text;
	public String source;
	public String sourceId;
	public String tag;
	public String autoTag;
	public ObjectId dbId;

	@Override
	public String toString() {
		return "Post[user(" + user + "), date(" + date.toString() + "), text("
				+ text + "), source(" + source + "), sourceId(" + sourceId
				+ ")]";
	}
	
	public static JSONObject toJSONObject(Post p) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("uid", p.uid);
			ret.put("user", p.user);
			ret.put("time", p.date.getTime());
			ret.put("text", p.text);
			ret.put("source", p.source);
			ret.put("sourceId", p.sourceId);
			ret.put("tag", p.tag);
			ret.put("autoTag", p.autoTag);
			ret.put("_id", p.dbId.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static BasicDBObject toDBObject(Post p) {
		BasicDBObject ret = new BasicDBObject("owner", p.owner);
		ret.append("uid", p.uid);
		ret.append("user", p.user);
		ret.append("time", p.date.getTime());
		ret.append("text", p.text);
		ret.append("source", p.source);
		ret.append("sourceId", p.sourceId);
		ret.append("tag", p.tag);
		ret.append("autoTag", p.autoTag);
		ret.append("_id", p.dbId);
		return ret;
	}
	
	public static Post fromDBObject(DBObject dbObj) {
		Post ret = new Post();
		BasicDBObject obj = (BasicDBObject) dbObj;
		ret.owner = obj.getString("owner");
		ret.uid = obj.getString("uid");
		ret.user = obj.getString("user");
		ret.date = new Date(obj.getLong("time"));
		ret.text = obj.getString("text");
		ret.source = obj.getString("source");
		ret.sourceId = obj.getString("sourceId");
		ret.tag = obj.getString("tag");
		ret.autoTag = obj.getString("autoTag");
		ret.dbId = obj.getObjectId("_id");
		return ret;
	}
}
