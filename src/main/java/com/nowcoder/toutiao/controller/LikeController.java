package com.nowcoder.toutiao.controller;

import com.nowcoder.toutiao.async.EventModel;
import com.nowcoder.toutiao.async.EventProducer;
import com.nowcoder.toutiao.async.EventType;
import com.nowcoder.toutiao.model.EntityType;
import com.nowcoder.toutiao.model.HostHolder;
import com.nowcoder.toutiao.model.News;
import com.nowcoder.toutiao.service.LikeService;
import com.nowcoder.toutiao.service.NewsService;
import com.nowcoder.toutiao.util.ToutiaoUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
public class LikeController {
    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    NewsService newsService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET, RequestMethod.POST})//访问入口
    @ResponseBody
    public String like(@Param("newId") int newsId){
        int userId = hostHolder.getUser().getId();
        long likeCount = likeService.like(userId, EntityType.ENTITY_NEWS, newsId);
        //更新like（点赞）的数量
        News news = newsService.getById(newsId);
        newsService.updateLikeCount(newsId, (int) likeCount);

        //发生like时，构造event，event把现场记录下来（1、2、3、4），然后将整个事件发出去
        //1、事件EventType.LIKE（点赞）
        //2、谁发生的setActorId(hostHolder.getUser().getId())
        //3、事件（点赞）的对象EntityId、EntityType
        //4、事件对象的owner是谁
        eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setActorId(hostHolder.getUser().getId()).setEntityId(newsId)
                .setEntityType(EntityType.ENTITY_NEWS).setEntityOwnerId(news.getUserId()));

        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.GET, RequestMethod.POST})//访问入口
    @ResponseBody
    public String disLike(@Param("newId") int newsId){
        int userId = hostHolder.getUser().getId();
        long likeCount = likeService.disLike(userId, EntityType.ENTITY_NEWS, newsId);
        newsService.updateLikeCount(newsId, (int) likeCount);
        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
