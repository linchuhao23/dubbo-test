package com.lin;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lin.service.MessageService;

public class ApplicationTest {
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
		
		MessageService messageService = context.getBean("messageService", MessageService.class);
		
		messageService.consumer("123");
		
	}

}
