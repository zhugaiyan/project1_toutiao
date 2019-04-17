package com.nowcoder.toutiao.model;

import org.springframework.stereotype.Component;

//HostHolder是用于存用户的
//可以通过set的方式存下来
//也可以通过get方式提取出来
//但是服务器又很多用户在用，只有一个Component，所以每条线程存自己的东西
//ThreadLocal：线程本地变量；get是只能get自己线程的内容
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser(){
        return users.get();
    }

    public void setUser(User user){
        users.set(user);
    }

    public void clear(){
        users.remove();
    }
}
