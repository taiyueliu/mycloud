package com.csxm.demo.receiver;

import com.csxm.demo.api.StreamClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * @Author 刘太月
 * @Despriction
 * @Created in 2020/3/17 10:33
 * @version: 1.0
 * <p>copyright: Copyright (c) 2018</p>
 */

@Component
@EnableBinding(value = {StreamClient.class})
public class StreamReceiver {
    @StreamListener(StreamClient.INPUT)
    public void receive(String message) {
        System.out.println(message);
    }

}
