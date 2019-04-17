package com.nowcoder.toutiao.service;

import com.nowcoder.toutiao.dao.NewsDAO;
import com.nowcoder.toutiao.model.News;
import com.nowcoder.toutiao.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class NewsService {
    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);
    @Autowired
    private NewsDAO newsDAO;

    public List<News> getLatestNews(int userId, int offset, int limit){
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }

    public int addNews(News news){
        newsDAO.addNews(news);
        return news.getId();
    }

    public News getById(int newsId) {
        return newsDAO.getById(newsId);
    }

    public int updateCommentCount(int id, int count) {
        return newsDAO.updateCommentCount(id, count);
    }

    public String saveImage(MultipartFile file ) throws IOException{
        //若保存成功，返回本地地址
        //判断后准名是否相同，jip、png。。。。。。
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if(dotPos < 0){
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
        if(!ToutiaoUtil.isFileAllowed(fileExt)){
            return null;
        }
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
        //给图片随机产生UUID的文件名
        Files.copy(file.getInputStream(), new File(ToutiaoUtil.IMAGE_DIR + fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        return ToutiaoUtil.TOUTIAO_DOWAIN + "image?name=" + fileName;
    }

    public int updateLikeCount(int id, int count){
        return newsDAO.updateLikeCount(id, count);
    }
}
