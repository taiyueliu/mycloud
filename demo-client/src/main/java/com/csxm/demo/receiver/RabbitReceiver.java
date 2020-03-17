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
            value = @Queue("myDirectQueue"),
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
                            value = @Queue("myFanoutQueue-one"),
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
