package com.nowcoder.toutiao.async;

import java.util.HashMap;
import java.util.Map;

//表示刚刚发生事件的数据都打包在这里
public class EventModel {
    private EventType type;
    private  int actorId;//事件触发者
    private int entityType;//事件触发对象entityType， entityId
    private  int entityId;
    private  int entityOwnerId;//触发对象的拥有者
    private Map<String, String> exts = new HashMap<String ,String>();//触发事件的现场（数据、参数）

    public String getExt(String key){
        return exts.get(key);
    }
    public EventModel setExt(String key, String value){
        exts.put(key, value);
        return this;
    }
    public EventModel(EventType type){
        this.type = type;
    }
    public EventModel(){

    }
    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public void setExts(Map<String, String> exts) {
        this.exts = exts;
    }
}
