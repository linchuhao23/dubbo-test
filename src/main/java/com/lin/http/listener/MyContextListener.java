package com.lin.http.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;

import org.springframework.stereotype.Component;

/**
 * 在tomcat下可以正常运行，在tomcat:run下没有效果
 * @author Administrator
 *
 */
@Component
@WebListener
public class MyContextListener implements ServletContextListener, ServletRequestListener {

    public void contextDestroyed(ServletContextEvent event)  { 
    	
    }

    public void contextInitialized(ServletContextEvent event)  { 
    	System.out.println("MyContextListener -> contextInitialized");
    }

	@Override
	public void requestDestroyed(ServletRequestEvent sre) {
		
	}

	@Override
	public void requestInitialized(ServletRequestEvent sre) {
		System.out.println("requestInitialized" + sre.getServletContext());
	}
	
}
