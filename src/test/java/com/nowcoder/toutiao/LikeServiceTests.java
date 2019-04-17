package com.nowcoder.toutiao;

import com.nowcoder.toutiao.service.LikeService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by zhuhanyou on 2019/2/26.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class LikeServiceTests {
    @Autowired
    LikeService likeService;
    //测试用例
    @Test
    public void testLike(){
        likeService.like(123, 1, 1);
        //Assert断言，确认1和likeService.getLikeStatus(123, 1, 1)相等，否则报错
        Assert.assertEquals(1, likeService.getLikeStatus(123, 1, 1));
    }
    @Test
    public void testDislike(){
        likeService.disLike(123, 1, 1);
        Assert.assertEquals(-1, likeService.getLikeStatus(123, 1, 1));
    }

    //异常测试，知道肯定会抛异常
    @Test(expected = IllegalArgumentException.class)
    public void testException(){
        throw new IllegalArgumentException("异常");
    }
    //初始化数据
    @Before
    public void setUp(){
        System.out.println("setUp");
    }
    //清理数据
    @After
    public void tearDown(){
        System.out.println("tearDown");
    }
    //跑所有测试用例之前
    @BeforeClass
    public static void beforeClass(){
        System.out.println("beforeClass");
    }
    //跑所有测试用例之后
    @AfterClass
    public static void afterClass(){
        System.out.println("afterClass");
    }
}
