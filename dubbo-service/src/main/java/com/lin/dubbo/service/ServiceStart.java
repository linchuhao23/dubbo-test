package com.lin.dubbo.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceStart {

	public static void main(String[] args) {
		//Main.main(args);
		@SuppressWarnings("resource")
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("application.xml");
		boolean running = false;
		try {
			applicationContext.start();
			running = true;
		} catch (Exception e) {
			applicationContext.stop();
		}
		synchronized (ServiceStart.class) {
            while (running) {
                try {
                	ServiceStart.class.wait();
                } catch (Throwable e) {
                }
            }
        }
	}

}
