package com.nowcoder.toutiao;

import com.nowcoder.toutiao.dao.CommentDAO;
import com.nowcoder.toutiao.dao.LoginTicketDAO;
import com.nowcoder.toutiao.dao.NewsDAO;
import com.nowcoder.toutiao.dao.UserDAO;
import com.nowcoder.toutiao.model.*;
import com.nowcoder.toutiao.util.JedisAdapter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class JedisTests {

	@Autowired
	JedisAdapter jedisAdapter;

	@Test
	public void testObject() {
		User user = new User();
		user.setHeadUrl("http://image.nowcoder.com/head/100t.png");
		user.setName("user1");
		user.setPassword("pwd");
		user.setSalt("salt");

		jedisAdapter.setObject("user1xx", user);//将数据存进jedis
		User u = jedisAdapter.getObject("user1xx", User.class);

		System.out.println(ToStringBuilder.reflectionToString(u));

	}
}
