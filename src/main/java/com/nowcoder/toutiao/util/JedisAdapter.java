package com.nowcoder.toutiao.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;


@Service
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);


    public static void print(int index, Object obj){
        System.out.println(String.format("%d, %s", index, obj.toString()));
    }

    public static void mainx(String[] argv){
        Jedis jedis = new Jedis();//默认链接到127.0.0.1.6379端口
        jedis.flushAll();//删除所有数据

        //Map<String, String> map = new HashMap<>()
        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "newhello");
        print(1, jedis.get("newhello"));
        jedis.setex("hello2", 15, "world");//设置插入hello2，并且15s过期

        jedis.set("pv", "100");
        jedis.incr("pv");//加1
        print(2, jedis.get("pv"));
        jedis.incrBy("pv", 5);//加5,便于数值型的变量
        print(2, jedis.get("pv"));

        //列表操作
        String listName = "listA";
        for(int i = 0; i <10; i++){
            jedis.lpush(listName, "a" + String.valueOf(i));
        }
        print(3, jedis.lrange(listName, 0, 12));
        print(4, jedis.llen(listName));
        print(5, jedis.lpop(listName));
        print(6, jedis.llen(listName));
        print(7, jedis.lindex(listName, 3));
        print(8, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "xx"));//将xx插入到a4的后面
        print(9, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "bb"));
        print(10, jedis.lrange(listName, 0, 12));

        String userKey = "userxx";//不定的属性比较适合用hset，临时添加字段
        jedis.hset(userKey, "name", "jim");
        jedis.hset(userKey, "age", "12");
        jedis.hset(userKey, "phone", "16668888");
        print(12, jedis.hget(userKey, "name"));//打印属性name
        print(13, jedis.hgetAll(userKey));//打印所有属性
        jedis.hdel(userKey, "phone");
        print(14, jedis.hgetAll(userKey));
        print(15, jedis.hkeys(userKey));
        print(16, jedis.hvals(userKey));
        print(17, jedis.hexists(userKey, "email"));
        print(18, jedis.hexists(userKey, "age"));
        jedis.hsetnx(userKey, "school", "zhonghua");//不存在school，则添加
        jedis.hsetnx(userKey, "name", "zgy");//若不存在，则添加；存在，则保持原始信息不变；已存在name，name=jim
        print(19, jedis.hgetAll(userKey));

        //集合set
        String likeKeys1 = "newsLike1";
        String likeKeys2 = "newsLike2";
        for(int i = 0; i < 10; i++){
            jedis.sadd(likeKeys1, String.valueOf(i));
            jedis.sadd(likeKeys2, String.valueOf(2*i));
        }
        print(20, jedis.smembers(likeKeys1));
        print(21, jedis.smembers(likeKeys2));
        print(22, jedis.sinter(likeKeys1, likeKeys2));//两集合的交
        print(23, jedis.sunion(likeKeys1, likeKeys2));//两集合的并
        print(24, jedis.sdiff(likeKeys1, likeKeys2));//两集合的差异，likeKeys1中有，而likeKeys2中没有的
        jedis.srem(likeKeys1, "5");//删除likeKeys1中的5
        print(26, jedis.smembers(likeKeys1));//点赞后，看赞是否再集合中
        print(27, jedis.scard(likeKeys1));//有多少值
        jedis.smove(likeKeys2, likeKeys1, "14");//将14从likeKeys2中移到likeKeys1
        print(28, jedis.scard(likeKeys1));
        print(29, jedis.smembers(likeKeys1));

        //sorted sets;z开头，带分值，适合用于排行榜
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "jim");
        jedis.zadd(rankKey, 60, "Ben");
        jedis.zadd(rankKey, 90, "Lim");
        jedis.zadd(rankKey, 79, "lucy");
        jedis.zadd(rankKey, 85, "Tom");
        print(30, jedis.zcard(rankKey));//有多少值
        print(31, jedis.zcount(rankKey, 61, 100));//61-100有多少
        print(32, jedis.zscore(rankKey, "lucy"));//lucy的分值
        jedis.zincrby(rankKey, 2, "lucy");//给lucy加2分
        print(33, jedis.zscore(rankKey, "lucy"));
        jedis.zincrby(rankKey, 2, "luc");//对于不存在的对象，则存储信息，luc有2分
        print(34, jedis.zcount(rankKey, 0, 100));
        print(35, jedis.zrange(rankKey, 1, 3));//打印第一名到第三名,从小到大排序
        print(36, jedis.zrevrange(rankKey, 1, 3));//从大到小排序，然后打印

        for(Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, "0", "100")){
            print(37, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }

        print(38, jedis.zrank(rankKey, "Ben"));//Ben的正排名次
        print(39, jedis.zrevrank(rankKey, "Ben"));//逆排名次

        JedisPool pool = new JedisPool();//连接池，默认8个线程
        for(int i = 0; i < 100; i++){
            Jedis j = pool.getResource();
            j.get("a");
            System.out.println("POOL" + i);
            j.close();//因为只有8个连接池，故用完后将线程关闭，实际是将资源放回
        }


    }

    private JedisPool pool = null;
    private  Jedis jedis = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost", 6379);
    }

    public String get(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.get(key);
        }catch(Exception e){
            logger.error("发生异常" + e.getMessage());
            return null;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public void set(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            jedis.set(key, value);
        }catch (Exception e){
            logger.error("发生异常" + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public long lpush(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        }catch (Exception e){
            logger.error("发生异常" + e.getMessage());
            return 0;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout, String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        }catch (Exception e){
            logger.error("发生异常" + e.getMessage());
            return null;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    private Jedis getJedis(){
        return pool.getResource();
    }

    //点赞功能
    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    //取消点赞
    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    //是否在点赞行列
    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
//点赞数量
    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setObject(String key, Object obj){//增加对象
        set(key, JSON.toJSONString(obj));
    }
    public <T> T getObject(String key, Class<T> clazz){//获取对象
        String value = get(key);
        if(value != null){
            return JSON.parseObject(value, clazz);
        }
        return null;
    }

}
