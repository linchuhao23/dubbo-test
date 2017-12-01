package com.lin.service;

public interface MessageService {
	
	/**
	 * 消费掉信息
	 * @param msg
	 */
	void consumer(String msg);

}
