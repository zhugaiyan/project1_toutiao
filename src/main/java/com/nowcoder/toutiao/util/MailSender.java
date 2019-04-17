package com.nowcoder.toutiao.util;

import com.nowcoder.toutiao.controller.LoginController;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import java.util.Map;
import java.util.Properties;

@Service
public class MailSender implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    public boolean sendWithHTMLTemplate(String to, String subject,
                                        String template, Map<String, Object> model){
        try{
            String nick = MimeUtility.encodeText("hello");//谁发的，邮件名
            InternetAddress from = new InternetAddress(nick + "<2943933994@qq.com>");//邮件地址
            MimeMessage mimeMessage = mailSender.createMimeMessage();//构造一份邮件
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            //利用velocity的引擎VelocityEngineUtils，把一些参数传进一份模板中
            String result = VelocityEngineUtils
                    .mergeTemplateIntoString(velocityEngine, template, "UTF-8", model);
            mimeMessageHelper.setTo(to);//谁发的
            mimeMessageHelper.setFrom(from);//发给谁
            mimeMessageHelper.setSubject(subject);//主题
            mimeMessageHelper.setText(result, true);//内容
            mailSender.send(mimeMessage);//发送
            return true;
        }catch (Exception e){
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("2943933994@qq.com");//邮箱
        mailSender.setPassword("zvzqotjyxtvbddah");//密码
        mailSender.setHost("smtp.qq.com");//发送邮件服务器
        mailSender.setPort(465);//端口
        mailSender.setProtocol("smtps");//协议
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);//使用ssl
        mailSender.setJavaMailProperties(javaMailProperties);
    }
}
