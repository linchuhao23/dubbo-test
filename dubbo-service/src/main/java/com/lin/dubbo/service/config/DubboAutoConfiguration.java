//package com.lin.dubbo.service.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.alibaba.dubbo.config.ApplicationConfig;
//import com.alibaba.dubbo.config.ProtocolConfig;
//import com.alibaba.dubbo.config.RegistryConfig;
//import com.alibaba.dubbo.config.spring.AnnotationBean;
//
////@Configuration  
//public class DubboAutoConfiguration { 
//      
//    @Bean  
//    public static AnnotationBean annotationBean() {  
//        AnnotationBean annotationBean = new AnnotationBean();  
//        annotationBean.setPackage("com.lin.dubbo.service.impl");  
//        return annotationBean;  
//    }  
//  
//    @Bean  
//    public ApplicationConfig applicationConfig() {  
//        ApplicationConfig applicationConfig = new ApplicationConfig();  
//        applicationConfig.setName("dubbo-service");  
//        return applicationConfig;  
//    }  
//  
//    @Bean  
//    public ProtocolConfig protocolConfig() {  
//        ProtocolConfig protocolConfig = new ProtocolConfig();  
//        protocolConfig.setName("dubbo");  
//        protocolConfig.setPort(-1);  
//        return protocolConfig;  
//    }  
//  
//    @Bean  
//    public RegistryConfig registryConfig() {  
//        RegistryConfig registryConfig = new RegistryConfig();  
//        registryConfig.setAddress("zookeeper://127.0.0.1:2181");  
//        return registryConfig;  
//    }  
//      
//}  