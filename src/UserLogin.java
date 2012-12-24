

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.usns.DB;

/**
 * Servlet implementation class User
 */
public class UserLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserLogin() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		JSONObject ret = new JSONObject();
		if (username == null || username.equals("") || password == null || password.equals("")) {
			try {
				ret.put("result", 1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			DBCollection userColl = DB.getCollection("user");
			BasicDBObject query = new BasicDBObject("username", username);
			DBCursor cursor = userColl.find(query);
			if (cursor.count() > 0) {
				//username exists
				if (cursor.next().get("password").equals(password)) {
					//password match
					try {
						//store in session
						request.getSession().setAttribute("user", username);
						ret.put("result",0);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					//password mismatch
					try {
						ret.put("result",2);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else {
				//username doesn't exist
				try {
					userColl.insert(new BasicDBObject("username", username).append("password", password));
					//store in session
					request.getSession().setAttribute("user", username);
					ret.put("result",0);
				} catch (JSONException e) {
					e.printStackTrace();
				}
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
		doGet(request, response);
	}

}
