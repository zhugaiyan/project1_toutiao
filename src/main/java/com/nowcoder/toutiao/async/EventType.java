package com.nowcoder.toutiao.async;

//发生了什么事件
//枚举,存储事件的名称
public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3);

    private int value;
    EventType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

}
