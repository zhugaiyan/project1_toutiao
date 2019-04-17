package com.nowcoder.toutiao.service;

import com.nowcoder.toutiao.dao.CommentDAO;
import com.nowcoder.toutiao.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    private  CommentDAO commentDAO;

    public List<Comment> getCommentsByEntity(int entityId, int entityType){
        return commentDAO.selectByEntity(entityId, entityType);
    }

    public int addComment(Comment comment){
        return commentDAO.addComment(comment);
    }

    public int getCommentCount(int entityId, int entityType){
        return commentDAO.getCommentCount(entityId, entityType);
    }

    //删除评论，对应CommentDAO中的update，实际是将status的值设置为1
    public void deleteComment(int entityId, int entityType){
        commentDAO.updateStatus(entityId, entityType, 1);
    }
}
