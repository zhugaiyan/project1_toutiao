package com.nowcoder.toutiao.controller;

import com.nowcoder.toutiao.async.EventModel;
import com.nowcoder.toutiao.async.EventProducer;
import com.nowcoder.toutiao.async.EventType;
import com.nowcoder.toutiao.service.UserService;
import com.nowcoder.toutiao.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

//登陆注册
@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.GET, RequestMethod.POST})//访问入口
    @ResponseBody
    public String reg(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "rember", defaultValue = "0") int rememberme,//是否记住登陆，0表示未登陆
                      HttpServletResponse response){
        try{
            Map<String, Object> map = userService.register(username, password);
            //如果注册成功，需要把数据写到用户的cookie里
            if(map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");//设置cookie全栈有效
                if(rememberme > 0){
                    cookie.setMaxAge(3600*24*5);//s，h,day
                }
                response.addCookie(cookie);
                return ToutiaoUtil.getJSONString(0, "注册成功");
            }else{
                return ToutiaoUtil.getJSONString(1, map);
            }
            //json格式：{"code":0 "mag":"xxxx"}
            //html格式：<b>xml</b>
        }catch (Exception e){
            logger.error("注册异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "注册异常");
        }
    }

    @RequestMapping(path = {"/login/"}, method = {RequestMethod.GET, RequestMethod.POST})//访问入口
    @ResponseBody
    public String login(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "rember", defaultValue = "0") int rememberme,
                        HttpServletResponse response){
        try{
            Map<String, Object> map = userService.login(username, password);
            //包含ticket则注册成功，否则注册失败
            if(map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");//设置cookie全栈有效
                if(rememberme > 0){
                    cookie.setMaxAge(3600*24*5);//s，h,day
                }
                response.addCookie(cookie);

                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                        .setActorId((int) map.get("userId")).setExt("username", username)
                        .setExt("email", "1918691500@qq.com"));

                return ToutiaoUtil.getJSONString(0, "登陆成功");

            }else{
                return ToutiaoUtil.getJSONString(1, map);
            }
        }catch (Exception e){
            logger.error("登陆异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "登陆异常");
        }
    }

    @RequestMapping(path = {"/logout/"}, method = {RequestMethod.GET, RequestMethod.POST})//访问入口
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }

}
