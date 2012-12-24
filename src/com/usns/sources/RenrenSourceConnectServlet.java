package com.usns.sources;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.renren.api.client.RenrenApiConfig;

/**
 * Servlet implementation class RenrenSourceConnectServlet
 */
public class RenrenSourceConnectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RenrenSourceConnectServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

  private static String URL = "https://graph.renren.com/oauth/authorize?client_id=" +
    RenrenApiConfig.renrenAppID + "&response_type=code&scope=read_user_feed&redirect_uri=" +
    RenrenApiConfig.renrenAppRedirectURI;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect(URL);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect(URL);
	}

}
