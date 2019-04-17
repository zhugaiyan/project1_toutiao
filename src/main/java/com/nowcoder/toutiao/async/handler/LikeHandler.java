package com.nowcoder.toutiao.async.handler;

import com.nowcoder.toutiao.async.EventHandler;
import com.nowcoder.toutiao.async.EventModel;
import com.nowcoder.toutiao.async.EventType;
import com.nowcoder.toutiao.model.Message;
import com.nowcoder.toutiao.model.User;
import com.nowcoder.toutiao.service.MessageService;
import com.nowcoder.toutiao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        //给点赞者发邮件
        Message message = new Message();
        message.setFromId(3);
        message.setToId(model.getActorId());//给自己发，为了演示效果
        User user = userService.getUser(model.getActorId());//得到发邮件的人
        message.setContent("用户" + user.getName()
                + "赞了你的资讯，http://127.0.0.1:8080/news/" + model.getEntityId());
        message.setCreatedDate(new Date());
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);//只关心点赞，发生点赞需要调用doHandle
    }
}
