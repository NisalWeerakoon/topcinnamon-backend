package com.Cinnamon_Miracle.Cinnamon_Miracle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConConfig implements WebMvcConfigurer{

	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
		.allowedOrigins("*")
		.allowedMethods("GET" , "POST" , "PUT" ,"DELETE")
		.allowedHeaders("*");
	}
}
