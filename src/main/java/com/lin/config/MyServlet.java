package com.lin.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.servlet.DispatcherServlet;

public class MyServlet extends DispatcherServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void initFrameworkServlet() throws ServletException {
		ServletContext servletContext = getServletContext();
		System.out.println("initFrameworkServlet" + servletContext);
	}

}
