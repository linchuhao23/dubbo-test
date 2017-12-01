package com.lin.redis;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisListenerConfiguration implements ApplicationContextAware {

	@Bean
	public RedisMessageListenerContainer contain(RedisConnectionFactory factory) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(factory);
		return container;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		final RedisMessageListenerContainer messageListenerContainer = applicationContext.getBean(RedisMessageListenerContainer.class);
		if (messageListenerContainer != null) {
			applicationContext.getBeansOfType(RedisListener.class).values().forEach(e -> {
				messageListenerContainer.addMessageListener(e, e.getTopic());
			});
		}
	}

}
