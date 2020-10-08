package ai.yunxi.im.route.consumer;

import ai.yunxi.im.common.constant.KakfaTopicConstant;
import ai.yunxi.im.common.pojo.ChatInfo;
import ai.yunxi.im.common.pojo.ImRouterRequestMessage;
import ai.yunxi.im.route.service.RouteService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.print.Book;

/**
 * Created by zn on 2020/9/15.
 */
@Slf4j
@Component
public class ChatConsumer {

    private final Gson gson = new Gson();
    @Resource
    private RouteService routeService;

    // 简单消费者
    @KafkaListener(groupId = "group1", topics = KakfaTopicConstant.CHAT)
    public void consumer1_1(ConsumerRecord<String, ImRouterRequestMessage> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, Consumer consumer, Acknowledgment ack) {
        log.info("1单独消费者消费消息group1,topic= {} ,content = {}",topic,record.value());
//        log.info("consumer content = {}",consumer);
        ImRouterRequestMessage imRouterRequestMessage = gson.fromJson(String.valueOf(record.value()),ImRouterRequestMessage.class);
        ack.acknowledge();
        routeService.zkSend(imRouterRequestMessage.getData());
        /*
         * 如果需要手工提交异步 consumer.commitSync();
         * 手工同步提交 consumer.commitAsync()
         */
    }

    // 简单消费者
//    @KafkaListener(groupId = "group1", topics = KakfaTopicConstant.CHAT)
    public void consumer1_2(ConsumerRecord<String, ImRouterRequestMessage> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, Consumer consumer, Acknowledgment ack) {
        log.info("2单独消费者消费消息group1_2,topic= {} ,content = {}",topic,record.value());
//        log.info("consumer content = {}",consumer);
        ack.acknowledge();


        /*
         * 如果需要手工提交异步 consumer.commitSync();
         * 手工同步提交 consumer.commitAsync()
         */
    }

//    @Resource
//    KafkaListenerContainerFactory kafkaListenerContainerFactory ;
//
//    @KafkaListener(topics = KakfaTopicConstant.CHAT)
//    public void receive(ImRouterRequestMessage message) {
//        System.out.println(kafkaListenerContainerFactory);
//        log.info(gson.toJson(message));
//    }
}
