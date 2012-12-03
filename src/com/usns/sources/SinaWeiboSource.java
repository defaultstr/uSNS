/* vi:set noet: */
package com.usns.sources;

import com.usns.DB;
import com.usns.entities.Post;
import java.util.ArrayList;
import weibo4j.Timeline;
import weibo4j.model.Status;
import weibo4j.model.StatusWrapper;
import weibo4j.model.WeiboException;
import com.mongodb.DBCollection;

public class SinaWeiboSource {
	public static String SOURCE_NAME = "SinaWeibo";

	public static ArrayList<Post> getTokenPostsSince(String token, String sinceId) {
		Timeline tim = new Timeline();
		tm.client.setToken(access_token);
		ArrayList<Post> result = null;
		try {
			StatusWrapper sw = tm.getHomeTimeline(0, 0, new Paging(1, 100, Long.parseLong(sinceId));
			for (Status status : sw.getStatuses()) {
				Post post = new Post();
				post.user = status.getUser().getId();
				post.source = SOURCE_NAME;
				post.sourceId = status.getId();
				post.date = status.getCreatedAt();
				post.text = status.getText();
				result.add(post);
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static ArrayList<Post> getUserPostsSince(String user, String sinceId) {
		DBCollection tokens = DB.getCollection("tokens");
		DBObject token = tokens.findOne(new BasicDBObject("user", user).append("source", SOURCE_NAME));
		if (token == null) return null;
		return getTokenPostsSince(token.get("access-token"), sinceId);

		/*Oauth oauth = new Oauth();
		String url = oauth.authorize("code", "getUserPosts(" + sinceId + ")");*/
		// Now we need to redirect our browser to |url|.
		// And deal with the callback url. When it's done, resume by calling
		// getTokenPostsSince. Assuming we have the code, we could get the access token
		// with this code: oauth.getAccessTokenByCode(code).getAccessToken();
	}

	public static void putNewUserToken(String user, String code) {
		DBCollection tokens = DB.getCollection("tokens");
		AccessToken at = new Oauth().getAccessTokenByCode(code);
		tokens.insert(new BasicDBObject("user", user).append("source", SOURCE_NAME)
										      .append("access-token", at.getAccessToken()).append("refresh-token", at.getRefreshToken()));
	}
}

