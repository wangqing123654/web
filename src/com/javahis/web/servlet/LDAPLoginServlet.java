package com.javahis.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author whaosoft
 *
 */
public class LDAPLoginServlet extends HttpServlet{


	/**
	 *
	 */
	private static final long serialVersionUID = -2475275938818604529L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String ldapusername = req.getParameter("n");
		String ldapuserpw = req.getParameter("pw");
		//...


		//
		req.setAttribute("ldapuser", ldapusername);
		req.setAttribute("ldappw", ldapuserpw);
		req.getRequestDispatcher("ldaplogin.jsp").forward(req, resp);
	}

}
