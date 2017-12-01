package com.lin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lin.ApplicationConfiguration;
import com.lin.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {
	
	@Autowired
	ApplicationConfiguration applicationConfiguration;

	@Override
	public void consumer(String msg) {
		System.out.println("MessageServiceImpl to Consumer message[" + msg + "]");
	}

}
