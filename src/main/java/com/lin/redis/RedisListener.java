package com.lin.redis;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;

/**
 * redis的监听器类
 * @author Administrator
 *
 */
public interface RedisListener extends MessageListener {
	
	ChannelTopic getTopic();
	
}
