package com.csxm.client.controller;

import com.csxm.client.api.StreamClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 刘太月
 * @Despriction
 * @Created in 2020/3/17 10:34
 * @version: 1.0
 * <p>copyright: Copyright (c) 2018</p>
 */
@RestController
public class StreamController {
    @Autowired
    private StreamClient streamClient;

    @GetMapping("send")
    public void send() {
        streamClient.output().send(MessageBuilder.withPayload("Hello World...").build());
    }

}
