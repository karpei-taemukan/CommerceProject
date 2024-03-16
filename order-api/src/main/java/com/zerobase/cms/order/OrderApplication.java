package com.zerobase.cms.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;
//import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.context.annotation.ComponentScan;
//import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ServletComponentScan
@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
@EntityScan(basePackages = {"com.zerobase.cms.order.domain.model"})
        //,repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
@EnableJpaRepositories(basePackages = "com.zerobase.cms.order.domain.repository")
public class OrderApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}