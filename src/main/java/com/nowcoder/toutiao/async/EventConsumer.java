package com.nowcoder.toutiao.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.toutiao.controller.LoginController;
import com.nowcoder.toutiao.util.JedisAdapter;
import com.nowcoder.toutiao.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//取出队列里的数据，反序列化为当时事件的现场eventModel，找出事件对应的handler进行处理
//将所有事件联系起来
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    //遇到event，可以找到对应的Handler；每个EventType有哪些Handler需要处理，找出对应的Handler
    private Map<EventType, List<EventHandler>> config = new HashMap<EventType, List<EventHandler>>();
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        //遍历上下文里所有实现EventHandler的类
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        //将beans组织起来
        if(beans != null){
            for(Map.Entry<String, EventHandler> entry : beans.entrySet()){
                //取出所有的EventType
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
                for (EventType type : eventTypes){
                    if(!config.containsKey(type)){
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }

        //线程，不断取数据
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> events = jedisAdapter.brpop(0, key);//取事件
                    for (String message : events){
                        if(message.equals(key)){
                            continue;
                        }
                        //反序列化为事件现场eventModel
                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        //判断是否是未处理好的事件或者未注册的事件
                        if(!config.containsKey(eventModel.getType())){
                            logger.error("不能识别的事件");
                            continue;
                        }
                        for(EventHandler handler : config.get(eventModel.getType())){
                            handler.doHandle(eventModel);
                        }
                    }
                }

            }
        });
        thread.start();

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
