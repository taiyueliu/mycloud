package com.csxm.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/***
 *  EnableDiscoveryClient euraka 服务发现
 *  EnableFeignClients Feign 声明书调用
 *  EnableCircuitBreaker 开启断路器功能
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableCircuitBreaker
public class DemoClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoClientApplication.class, args);
	}

	/**
	 * LoadBalanced为RestTemplate开启负载均衡
	 * @return
	 */
	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
