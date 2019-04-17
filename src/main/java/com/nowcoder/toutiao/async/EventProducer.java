package com.nowcoder.toutiao.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.toutiao.util.JedisAdapter;
import com.nowcoder.toutiao.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//将数据发出去：把数据序列化后放进某个队列里
@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel model){
        try {
            String json = JSONObject.toJSONString(model);//将事件序列化
            String key = RedisKeyUtil.getEventQueueKey();//将事件放入队列
            jedisAdapter.lpush(key, json);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
