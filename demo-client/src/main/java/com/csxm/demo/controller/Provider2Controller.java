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
        send1();
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

}
