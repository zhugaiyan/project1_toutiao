package com.nowcoder.toutiao.controller;

import com.nowcoder.toutiao.model.*;
import com.nowcoder.toutiao.service.*;
import com.nowcoder.toutiao.util.ToutiaoUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.swing.text.View;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    HostHolder hostHolder;
    //表示当前用户是否登陆

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @RequestMapping(path = {"/news/{newsId}"}, method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId, Model model){
        try{
            News news = newsService.getById(newsId);
            if(news != null){
                int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
                if(localUserId != 0){
                    model.addAttribute("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
                }else{
                    model.addAttribute("like", 0);
                }
                //评论
                List<Comment> comments = commentService.getCommentsByEntity(news.getId(), EntityType.ENTITY_NEWS);
                List<ViewObject> commentVOs = new ArrayList<ViewObject>();
                for(Comment comment : comments){
                    ViewObject commentVO = new ViewObject();
                    commentVO.set("comment", comment);
                    commentVO.set("user", userService.getUser(comment.getUserId()));
                    commentVOs.add(commentVO);
                }
                model.addAttribute("comments", commentVOs);
            }
            model.addAttribute("news", news);
            model.addAttribute("owner", userService.getUser(news.getUserId()));
        }catch (Exception e){
            logger.error("获取资讯明细错误" + e.getMessage());
        }
        return  "detail";
    }

    @RequestMapping(path = {"/image"}, method = {RequestMethod.GET})
    @ResponseBody
    //图片展示
    public void getImage(@RequestParam("name") String imageName,
                         HttpServletResponse response){
        try {
            response.setContentType("image/jpg");//jpg表示图片格式
            StreamUtils.copy(new FileInputStream(new File(ToutiaoUtil.IMAGE_DIR + imageName)),
                    response.getOutputStream());
        }catch (Exception e){
            logger.error("读取图片错误" + e.getMessage());
        }
    }

    @RequestMapping(path = {"/user/addNews/"}, method = {RequestMethod.POST})//访问入口,上传数据一般用post
    @ResponseBody
    public String addNews(@RequestParam ("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link) {
        try {
            News news = new News();
            if(hostHolder.getUser() != null){
                news.setUserId(hostHolder.getUser().getId());
            }else{
                //匿名用户id
                news.setUserId(3);
            }
            news.setImage(image);
            news.setCreatedDate(new Date());
            news.setTitle(title);
            news.setLink(link);
            newsService.addNews(news);
            return ToutiaoUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("添加资讯错误" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "发布失败");
        }
    }

    @RequestMapping(path = {"/uploadImage/"}, method = {RequestMethod.POST})//访问入口,上传数据一般用post
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        //@RequestParam("file") MultipartFile file中存储了对象的所有核心数据
        try {
            //查看transferto方法，也是保存到某个目录
            //String fileUrl = newsService.saveImage(file);
            String fileUrl = qiniuService.saveImage(file);
            if(fileUrl == null){
                return ToutiaoUtil.getJSONString(1, "上传图片失败");
            }
            return ToutiaoUtil.getJSONString(0, fileUrl);
        } catch (Exception e) {
            logger.error("上传图片失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "上传失败");
        }
    }

    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content){
        try{
            Comment comment = new Comment();
            comment.setUserId(hostHolder.getUser().getId());
            comment.setContent(content);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setEntityId(newsId);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            commentService.addComment(comment);

            //更新news里的评论数量
            //其实可以异步实现，后面添加
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            newsService.updateCommentCount(comment.getEntityId(), count);
        }catch (Exception e){
            logger.error("提交评论错误" + e.getMessage());
        }
        return "redirect:/news/" + String.valueOf(newsId);
    }
}

