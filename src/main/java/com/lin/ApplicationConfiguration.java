package com.lin;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configurable
@ComponentScan(basePackages = {"com.lin"})
@EnableWebMvc
public class ApplicationConfiguration {
	
	public ApplicationConfiguration() {
		System.out.println("0----0");
	}

}
