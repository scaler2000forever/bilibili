package com.atlinlin.bilibili.service.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atlinlin.bilibili.domain.UserFollowing;
import com.atlinlin.bilibili.domain.UserMoment;
import com.atlinlin.bilibili.domain.constant.UserMomentsConstant;
import com.atlinlin.bilibili.service.UserFollowingService;
import com.mysql.cj.util.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @ author : LiLin
 * @ create : 2022-10-17 23:44
 */

/**
 * 订阅发布模式
 */
//@Configuration
public class RocketMQConfig {

    //配置属性
    @Value("${rocketmq.name.server.address}")
    private String nameServerAddr;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserFollowingService userFollowingService;

    //动态提醒的生产者与Bean一一对应
    @Bean("momentProducer")
    public DefaultMQProducer momentProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentsConstant.GROUP_MOMENTS);
        producer.setNamesrvAddr(nameServerAddr);
        producer.start();
        return producer;
    }

    @Bean("momentCustomer") //push推送方式，给所有订阅的
    public DefaultMQPushConsumer pushConsumer() throws MQClientException {
        DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_MOMENTS);
        pushConsumer.setNamesrvAddr(nameServerAddr);
        //订阅操作
        pushConsumer.subscribe(UserMomentsConstant.TOPIC_MOMENTS, "*");
        //监听器抓取消息
        pushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msg, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt message = msg.get(0);
                if (message == null) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                //转换类型
                String bodyStr = new String(message.getBody());
                UserMoment userMoment = JSONObject.toJavaObject(JSONObject.parseObject(bodyStr), UserMoment.class);
                //获取到发布者的userId->获得粉丝ID->推送到一个userMoments列表->
                Long userId = userMoment.getUserId();
                //获得关注用户的粉丝的id集合
                List<UserFollowing> fanList = userFollowingService.getUserFans(userId);
                //粉丝用redis接收
                for (UserFollowing fan : fanList) {
                    //用来区分那些粉丝订阅了这则消息
                    String key = "subscribed-" + fan.getUserId();
                    String subscribedListStr = redisTemplate.opsForValue().get(key);//获取的值
                    //将获取到值的转换为列表或者数组
                    List<UserMoment> subscribedList;
                    if(StringUtils.isNullOrEmpty(subscribedListStr)){
                        subscribedList = new ArrayList<>();
                    }else{
                        //转换类型的具体操作
                        subscribedList = JSONArray.parseArray(subscribedListStr, UserMoment.class);
                    }
                    subscribedList.add(userMoment);
                    //推送到redis
                    redisTemplate.opsForValue().set(key,JSONObject.toJSONString(subscribedList));
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        pushConsumer.start();
        return pushConsumer;
    }

}
