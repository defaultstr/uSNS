/* vi:set noet: */
package com.usns.sources;

import com.usns.entities.Post;
import java.util.ArrayList;
import weibo4j.Timeline;
import weibo4j.model.Status;
import weibo4j.model.StatusWrapper;
import weibo4j.model.WeiboException;

public class SinaWeiboSource {
	public static ArrayList<Post> getTokenPostsSince(String token, String sinceId) {
		Timeline tim = new Timeline();
		tm.client.setToken(access_token);
		ArrayList<Post> result = null;
		try {
			StatusWrapper sw = tm.getHomeTimeline(0, 0, new Paging(1, 100, Long.parseLong(sinceId));
			for (Status status : sw.getStatuses()) {
				Post post = new Post();
				post.user = status.getUser().getId();
				post.source = "SinaWeibo";
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
		String accessToken = null; // TODO: retrieve from db by |user|
		ArrayList<Post> result = getTokenPostsSince(accessToken, sinceId);
		if (result != null) return result;

		// Something is wrong. Now we need to display a web page?!
		Oauth oauth = new Oauth();
		String url = oauth.authorize("code", "getUserPosts(" + sinceId + ")");
		// Now we need to redirect our browser to |url|.
		// And deal with the callback url. When it's done, resume by calling
		// getTokenPostsSince. Assuming we have the code, we could get the access token
		// with this code: oauth.getAccessTokenByCode(code).getAccessToken();
		return null;
	}
}

