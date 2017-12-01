package com.lin.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class ChatRedisListener implements RedisListener {

	@Override
	public void onMessage(Message message, byte[] pattern) {
		System.out.println(message.getClass().getName());
		System.out.println(new String(message.getBody()));
	}

	@Override
	public ChannelTopic getTopic() {
		return new ChannelTopic("chat");
	}

	

}
