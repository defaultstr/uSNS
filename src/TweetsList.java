

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import weibo4j.Oauth;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.usns.DB;
import com.usns.entities.Post;
import com.usns.learning.GlobalClassifier;
import com.usns.learning.MyClassifier;
import com.usns.sources.SinaWeiboSource;
import com.usns.sources.RenrenSource;

/**
 * Servlet implementation class TweetsList
 */
public class TweetsList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TweetsList() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = (String) request.getSession().getAttribute("user");
		int startAt = 0;
		if (request.getParameter("startAt") != null) {
			startAt = Integer.parseInt(request.getParameter("startAt"));
		}
		int limit = 100;
		if (request.getParameter("limit") != null) {
			limit = Integer.parseInt(request.getParameter("limit"));
		}
		if (limit > 1000)
			limit = 1000;
		JSONObject ret = new JSONObject();
		if (user == null || user.equals("")) {
			try {
				ret.put("result", 3);
				ret.write(response.getWriter());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		}
		//get old posts in DB, using 100 past posts as train set
		DBCollection postColl = DB.getCollection("posts");
		BasicDBObject query = new BasicDBObject("owner", user);
		DBCursor cursor = postColl.find(query);
		cursor.limit(1000);
		cursor.sort(new BasicDBObject("time", -1));
		ArrayList<Post> oldPosts = new ArrayList<Post>();
		MyClassifier myClassifier = new MyClassifier();
		GlobalClassifier gClassifier = GlobalClassifier.getInstance();
		int trainCount = 0;
		while (cursor.hasNext()) {			
			Post p = Post.fromDBObject(cursor.next());
			if (oldPosts.size() < 1000)
				oldPosts.add(p);
			//train the user classifier
			if (trainCount < 100) {
				myClassifier.feedData(p, p.tag);
				gClassifier.feedData(p, p.tag);
				trainCount ++;
			}
		}
		//get new posts from sources
		ArrayList<Post> newPosts = new ArrayList<Post>();
		//first get all the sources this user subscribed
		DBCollection tokenColl = DB.getCollection("tokens");
		DBCursor sourceCursor = tokenColl.find(new BasicDBObject("user", user));
		ArrayList<String> sourceList = new ArrayList<String>();
		while (sourceCursor.hasNext()) {
			sourceList.add((String)sourceCursor.next().get("source"));
		}
		//for all the sources get new posts
		for (String source : sourceList) {
			System.out.println(source);
			//find the newest one...
			Post lastPost = null ;
			for (Post p : oldPosts) {
				if (p.source.equals(source)) {
					lastPost = p;
					break;
				}
			}
			String lastSourceId = null;
			if (lastPost != null) {
				lastSourceId = lastPost.sourceId;
				System.out.println(lastSourceId);
			}
			if (source.equals(SinaWeiboSource.SOURCE_NAME)) {
				ArrayList<Post> newList = SinaWeiboSource.getUserPostsSince(user, lastSourceId);
				if (newList == null) {
					response.sendRedirect("connectToWeibo");
				} else {
					newPosts.addAll(newList);
				}
			} else if (source.equals(RenrenSource.SOURCE_NAME)) {
        ArrayList<Post> newList = RenrenSource.getUserPostsSince(user, lastSourceId);
       
        if (newList == null) {
          response.sendRedirect("connectToRenren");
        } else {
			System.out.println(newList.size());
          newPosts.addAll(newList);
        }
      }
			//TODO add other source			
		}
		Collections.sort(newPosts);
		//tag and store new posts
		for (Post p : newPosts) {
			p.tag = MyClassifier.NOT_SPAM_TAG;
			p.autoTag = gClassifier.getTag(p);
			if (!p.autoTag.equals(GlobalClassifier.SPAM_TAG))
				p.autoTag = myClassifier.getTag(p);
			BasicDBObject obj = Post.toDBObject(p);
			postColl.insert(obj);
			p.dbId = obj.getObjectId("_id");
		}
		
		int index = 0;
		JSONArray tweetsList = new JSONArray();
		//first return the good new posts
		for (Post p : newPosts) {
			if (p.autoTag.equals(MyClassifier.GOOD_TAG)) {
				if (index >= startAt && index < startAt+limit) {
					tweetsList.put(Post.toJSONObject(p));
				}
				index ++;
				if (index >= startAt + limit)
					break;
			}
		}
		//then the normal posts
		for (Post p : newPosts) {
			if (!p.autoTag.equals(MyClassifier.SPAM_TAG)) {
				if (index >= startAt && index < startAt+limit) {
					tweetsList.put(Post.toJSONObject(p));
				}
				index ++;
				if (index >= startAt + limit)
					break;
			}
		}
		//finally the old posts
		for (Post p : oldPosts) {
			if (!p.tag.equals(MyClassifier.SPAM_TAG) && !p.autoTag.equals(MyClassifier.SPAM_TAG)) {
				if (index >= startAt && index < startAt+limit) {
					tweetsList.put(Post.toJSONObject(p));
				}
				index ++;
				if (index >= startAt + limit)
					break;
			}
		}
		try {
			response.setContentType("text/html;charset=utf-8");
			ret.put("result",0);
			ret.put("list", tweetsList);
			ret.write(response.getWriter());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
