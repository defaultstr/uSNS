

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.usns.DB;

/**
 * Servlet implementation class Tag
 */
public class Tag extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Tag() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = (String) request.getSession().getAttribute("user");
		String dbId = request.getParameter("_id");
		String tag = request.getParameter("tag");
		JSONObject ret = new JSONObject();
		if (user == null || user.equals("")) {
			try {
				ret.put("result", 3);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (dbId == null || dbId.equals("") || tag == null || tag.equals("")) {
			try {
				ret.put("result", 1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			ObjectId objId = new ObjectId(dbId);
			DBCollection postColl = DB.getCollection("posts");
			postColl.update(new BasicDBObject("_id", objId), 
					new BasicDBObject("$set", 
							new BasicDBObject("tag", tag)));
			try {
				ret.put("result", 0);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			ret.write(response.getWriter());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
