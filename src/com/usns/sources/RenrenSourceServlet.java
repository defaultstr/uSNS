package com.usns.sources;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.usns.entities.Post;

/**
 * Servlet implementation class RenrenSourceServlet
 */
public class RenrenSourceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RenrenSourceServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO split this method in two. Hell, we should split this class in two...
		String code = request.getParameter("code");
		String user = (String) request.getSession().getAttribute("user");
		RenrenSource.putNewUserToken(user, code);
		response.sendRedirect("../SOA.html?"+user);

	}

}
