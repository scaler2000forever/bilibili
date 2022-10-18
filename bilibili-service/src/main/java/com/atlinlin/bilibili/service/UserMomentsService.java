package com.atlinlin.bilibili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atlinlin.bilibili.dao.UserMomentsDao;
import com.atlinlin.bilibili.domain.UserMoment;
import com.atlinlin.bilibili.domain.constant.UserMomentsConstant;
import com.atlinlin.bilibili.service.util.RocketMQUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * @ author : LiLin
 * @ create : 2022-10-18 10:57
 */
@Service
public class UserMomentsService {

    @Autowired
    private UserMomentsDao userMomentsDao;

    //获取bean中MQ的生产者和消费者
    @Autowired
    private ApplicationContext applicationContext;

    //从redis中获取
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 用户发布动态
     * @param userMoment
     */
    public void addUserMoments(UserMoment userMoment) throws Exception {
        //先把需要添加的东西设置
        userMoment.setCreateTime(new Date());
        userMomentsDao.addUserMoments(userMoment);
        //添加到MQ
        DefaultMQProducer producer = (DefaultMQProducer) applicationContext.getBean("momentsProducer");
        Message msg = new Message(UserMomentsConstant.TOPIC_MOMENTS, JSONObject.toJSONString(userMoment).getBytes(StandardCharsets.UTF_8));
        RocketMQUtil.syncSengMsg(producer,msg);
    }

    public List<UserMoment> getUserSubscribedMoments(Long userId) {
        String key = "subscribed-" + userId;
        String listStr = redisTemplate.opsForValue().get(key);
        List<UserMoment> list = JSONArray.parseArray(listStr,UserMoment.class);
        return list;
    }
}
