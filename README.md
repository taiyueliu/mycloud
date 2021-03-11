### SpringCloud Config

#### 配置pom

```xml
<dependency>
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

#### 配置bootstrap.yml

1. config客户端和服务端 bootstrap.yml 都增加eureka配置

```yaml
eureka:
   client:
      serviceUrl:
         #设置与EurekaServer交互的地址,多个地址使用,分割--集群下这边为其他服务ip  必须为域名
          defaultZone: http://localhost:10000/eureka/
   instance:
      prefer-ip-address:  true
      ip-address: ${IP_ADDRESS:localhost}
      instance-id: ${spring.application.name}:${server.port}
```

2. config服务端

```yaml
server:
  port: 8887
spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: http://47.105.88.185:1080/myproject/{application}.git
          username: root
          password: lty19940915
          repos:
            security: ##匹配config-client名为security开头的
              pattern: security*/*
              uri: http://47.105.88.185:1080/security/{application}.git
              username: root
              password: lty19940915
            cloud:  ##匹配config-client名为cloud开头的
              pattern: cloud*/*
              uri: http://47.105.88.185:1080/cloud/{application}.git
              username: root
              password: lty19940915
```

3. config客户端

```
spring:
  profiles: dev
  cloud:
    config:
      discovery:
        enabled: true #开启eureka发现服务
        service-id: config-server #config服务名称 可启动eureka查看服务名
      fail-fast: true  #是否启动快速失败功能，功能开启则优先判断config server是否正常
      retry:
        initial-interval: 1000 #最初重试间隔为 1000 毫秒
        max-attempts: 6 #最多重试 6 次
        max-interval: 2000 #最长重试间隔为 2000 毫秒
        multiplier: 1.1 #每次重试失败后，重试间隔所增加的倍数
```

#### 快速重启

```
<!--config client服务自动重启-->
<dependency>
   <groupId>org.springframework.retry</groupId>
   <artifactId>spring-retry</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```



### rabbitMQ

#### 配置pom

```xml
<dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

#### 配置yml

```yaml
spring:
  rabbitmq:
    host: 47.105.88.185
    post: 5672
    username: guest
    password: guest
```

#### 生产者

```
package com.csxm.demo.controller;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @Author 刘太月
 * @Despriction
 * @Created in 2020/3/16 12:36
 * @version: 1.0
 * <p>copyright: Copyright (c) 2018</p>
 */
@RestController
@RequestMapping("/cs")
/**刷新配置*/
@RefreshScope
public class Provider2Controller {
    @Value("${form}")
    private String form;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @GetMapping("/getMsgFromProvider2")
    @ResponseBody
    public String getMsgFromProvider2(){
        return form;
    }

    @GetMapping("/getMsgFromProvider1")
    public void getMsgFromProvider1(){
        send3();
    }


    /**
     * Direct 直连交换机
     */
    private void send() {
        //(交换机,routingKey,消息内容)
        amqpTemplate.convertAndSend("myDirectExchange","mine.direct","this is a message");
    }

    /***
     * Default 默认交换机
     */
    private void send1() {
        //(交换机,routingKey,消息内容)
        amqpTemplate.convertAndSend("myDefaultQueue","this is a message 1");
    }

    /**
     * Fanout 扇型交换机
     */
    private void send2() {
        //(交换机,routingKey,消息内容),routingKey随意
        amqpTemplate.convertAndSend("myFanoutExchange","key.one","this is a message3");
    }

    /**
     * Topic 主题交换机
     */
    private void send3() {
        //模拟某人在商店买彩票中奖了
        amqpTemplate.convertAndSend("news-exchange","province.city.street.shop","有人中了大奖");
    }

}

```

#### 消费者

```
package com.csxm.demo.receiver;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author 刘太月
 * @Despriction
 * @Created in 2020/3/16 16:37
 * @version: 1.0
 * <p>copyright: Copyright (c) 2018</p>
 */
@Component
public class RabbitReceiver {

    /**
     * Direct 直连交换机 消息内容,当只有一个参数的时候可以不加@Payload注解
     * @param hello
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("myDirectQueue1"),
            exchange = @Exchange(value = "myDirectExchange",type = ExchangeTypes.DIRECT),
            key = "mine.direct"

    ))
    @RabbitHandler
    public void onMessage(@Payload String hello) {
            System.out.println("RabbitReceiver : "+hello);
    }

    /**
     * Default 默认交换机
     * @param msg
     */
    @RabbitListener(queuesToDeclare = @Queue("myDefaultQueue"))
    @RabbitHandler
    public void onMessage1(String msg) {
        System.out.println(msg);
    }

    //Fanout 扇型交换机
    @RabbitListeners({
            @RabbitListener(
                    bindings = @QueueBinding(
                            value = @Queue("myFanoutQueue-three"),
                            exchange = @Exchange(value = "myFanoutExchange", type = ExchangeTypes.FANOUT),
                            key = "key.one")),

            @RabbitListener(
                    bindings = @QueueBinding(
                            value = @Queue("myFanoutQueue-two"),
                            exchange = @Exchange(value = "myFanoutExchange", type = ExchangeTypes.FANOUT),
                            key = "key.two")),
    })
    @RabbitHandler
    public void onMessage2(@Payload String msg, @Headers Map<String, Object> headers) {
        System.out.println("来自" + headers.get(AmqpHeaders.CONSUMER_QUEUE) + "的消息:" + msg);
    }




    //Topic 主题交换机  通配符 # : 一个或多个word, * 一个word
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("province-news-queue"),
            exchange = @Exchange(value = "news-exchange", type = ExchangeTypes.TOPIC),
            key = "province.#"))
    @RabbitHandler
    public void provinceNews(String msg) {
        System.out.println("来自省TV的消息:" + msg);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("city-news-queue"),
            exchange = @Exchange(value = "news-exchange", type = ExchangeTypes.TOPIC),
            key = "province.city.#"))
    @RabbitHandler
    public void cityNews(String msg) {
        System.out.println("来自市TV的消息:" + msg);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("street-news-queue"),
            exchange = @Exchange(value = "news-exchange", type = ExchangeTypes.TOPIC),
            key = "province.city.street.*"))
    @RabbitHandler
    public void streetNews(String msg) {
        System.out.println("来自街区TV的消息:" + msg);
    }
}

```

