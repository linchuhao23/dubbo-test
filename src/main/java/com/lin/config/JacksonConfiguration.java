package com.lin.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

@SuppressWarnings("deprecation")
@Configuration
public class JacksonConfiguration {
	
	@Bean
	public AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter() {
		AnnotationMethodHandlerAdapter requestMappingHandlerAdapter = new AnnotationMethodHandlerAdapter();
		@SuppressWarnings("unchecked")
		HttpMessageConverter<Object>[] httpMessageConverter = new HttpMessageConverter[] {mappingJackson2HttpMessageConverter()}
;		requestMappingHandlerAdapter.setMessageConverters(httpMessageConverter );
		return requestMappingHandlerAdapter;
	}
	
	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.ALL));
		return mappingJackson2HttpMessageConverter;
	}

}
