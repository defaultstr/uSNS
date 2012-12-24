/* vi:set noet: */
package com.usns.sources;

import com.usns.DB;
import com.usns.entities.Post;
import java.util.ArrayList;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.renren.api.client.RenrenApiClient;
import com.renren.api.client.services.FeedService;
import com.renren.api.client.RenrenApiConfig;
import com.renren.api.client.param.impl.AccessToken;
import com.renren.api.client.utils.HttpURLUtils;

public class RenrenSource {
	public static final String SOURCE_NAME = "Renren";
	public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static ArrayList<Post> getTokenPostsSince(String owner, String token,
			String sinceId) {
		FeedService fs = RenrenApiClient.getInstance().getFeedService();
		ArrayList<Post> result = null;
		JSONArray ja = fs.getFeed("10,11", 0, 1, 100, new AccessToken(token));
		if (ja != null) {
			result = new ArrayList<Post>(ja.size());
			for (int i = 0; i < ja.size(); i++) {
				JSONObject jo = (JSONObject) ja.get(i);
				Post post = new Post();
				post.owner = owner;
				post.uid = jo.get("actor_id").toString();
				post.user = jo.get("name").toString();
				post.source = SOURCE_NAME;
				post.sourceId = jo.get("post_id").toString();
				post.date = df.parse(jo.get("update_time").toString());
				post.text = jo.get("content").toString();
				result.add(post);
			}
		} else {
			// If anything, we need to invalidate this token. It fails!
			// XXX Maybe use refresh token?
			DB.getCollection("tokens").remove(
					new BasicDBObject("access-token", token).append("source",
							SOURCE_NAME));
		}
		return result;
	}

	public static ArrayList<Post> getUserPostsSince(String user, String sinceId) {
		DBCollection tokens = DB.getCollection("tokens");
		DBObject token = tokens.findOne(new BasicDBObject("user", user).append(
				"source", SOURCE_NAME));
		if (token == null)
			return null;
		return getTokenPostsSince(user, (String) token.get("access-token"), sinceId);
	}

	public static ArrayList<Post> getUserPosts(String user) {
		return getUserPostsSince(user, null);
	}

	public static void putNewUserToken(String user, String code) {
		DBCollection tokens = DB.getCollection("tokens");
		try {
			String rrOAuthTokenEndpoint = "https://graph.renren.com/oauth/token";
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("client_id", RenrenApiConfig.renrenApiKey);
			parameters.put("client_secret", RenrenApiConfig.renrenApiSecret);
			parameters.put("redirect_uri", RenrenApiConfig.renrenAppRedirectURI);
			parameters.put("grant_type", "authorization_code");
			parameters.put("code", code);
			String tokenResult = HttpURLUtils.doPost(rrOAuthTokenEndpoint, parameters);
			JSONObject tokenJson = (JSONObject) JSONValue.parse(tokenResult);
			tokens.insert(new BasicDBObject("user", user)
					.append("source", SOURCE_NAME)
					.append("access-token", (String) tokenJson.get("access_token"))
					.append("refresh-token", (String) tokenJson.get("refresh_token"));
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
}
