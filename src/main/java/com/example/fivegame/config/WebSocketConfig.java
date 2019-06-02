package com.example.fivegame.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author white matter
 */
@Configuration
public class WebSocketConfig {
    /**
     * @Description: 检测服务类实现 是由Spring官方提供的标准实现，
     * 用于扫描ServerEndpointConfig配置类和@ServerEndpoint注解实例。
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }


    @Autowired
    private RequestListener requestListener;
    /**
     *需要把这个类注册为监听器
     * 这是使用@Bean注解的方式
     */
    @Bean
    public ServletListenerRegistrationBean<RequestListener> servletListenerRegistrationBean() {
        ServletListenerRegistrationBean<RequestListener> servletListenerRegistrationBean = new ServletListenerRegistrationBean<>();
        servletListenerRegistrationBean.setListener(requestListener);
        return servletListenerRegistrationBean;
    }
}
