package com.nowcoder.toutiao.controller;

import com.nowcoder.toutiao.model.User;
import com.nowcoder.toutiao.service.ToutiaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

//@Controller
public class IndexController {

 private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
 @Autowired
 private ToutiaoService toutiaoService;

 @RequestMapping(path = {"/", "index"}, method = {RequestMethod.GET, RequestMethod.POST})//访问入口
 @ResponseBody
 public String index(HttpSession session){
  logger.info("visit index");
  return "hello" + session.getAttribute("msg") +
   "<br> say:" + toutiaoService.say();
 }

 //下面一行是网址后面的：127.0.0.1:8080/profile/12/33?key=xx&type=33
 //type和key不是path中的参数，是Request中的
 @RequestMapping(value = {"/profile/{groupId}/{userId}"})
 @ResponseBody
 public String profile(@PathVariable("groupId") String groupId,
                       @PathVariable("userId") int userId,
                       @RequestParam(value = "type", defaultValue =  "1") int type,
                       @RequestParam(value = "key", defaultValue = "toutiao") String key){

  return String.format("GID{%s}, UID{%d}, TYPE{%d}, KEY{%s}", groupId, userId, type, key);

 }

 @RequestMapping(value = {"/vm"})
 public String news(Model model){
  model.addAttribute("value1", "vv1");
  List<String> colors = Arrays.asList(new String[] {"RED", "GREEN", "BLUE"});
  Map<String, String> map = new HashMap<String, String>();
  for(int i = 0; i < 4; i++){
   map.put(String.valueOf(i), String.valueOf(i * i));
  }

  model.addAttribute("colors", colors);
  model.addAttribute("map", map);
  model.addAttribute("user", new User("jim"));
  return "news";
 }

 @RequestMapping(value= {"/request"})
 @ResponseBody
 public String request(HttpServletRequest request,
                        HttpServletResponse response,
                        HttpSession session){

  StringBuilder sb = new StringBuilder();
  Enumeration<String> headerNames = request.getHeaderNames();
  while(headerNames.hasMoreElements()){
   String name = headerNames.nextElement();
   sb.append(name + ":" + request.getHeader(name) + "<br>");
  }
//cookie是以键值对存储的
  for(Cookie cookie : request.getCookies()){
   sb.append("Cookie");
   sb.append(cookie.getName());
   sb.append(":");
   sb.append(cookie.getValue());
   sb.append("<br>");
  }

  sb.append("getMethod:" + request.getMethod() + "<br>");
  sb.append("getPathInfo:" + request.getPathInfo() + "<br>");
  sb.append("getQueryString:" + request.getQueryString() + "<br>");
  sb.append("getRequestURI:" + request.getRequestURI() + "<br>");
  return sb.toString();

 }

 @RequestMapping(value= {"/response"})//生成cookie
 @ResponseBody
 public  String response(@CookieValue(value = "nowcoderid", defaultValue = "a") String nowcoderId,
                         @RequestParam(value = "key", defaultValue = "key") String key,
                         @RequestParam(value = "value", defaultValue = "value") String value,
                         HttpServletResponse response){
  response.addCookie(new Cookie(key, value));
  response.addHeader(key, value);
  return "NowCoderId from Cookie:" + nowcoderId;

 }

 @RequestMapping(value = "/redirect/{code}")
public String redirect(@PathVariable("code") int code,
                       HttpSession session){
  /*
  //默认是302（临时跳转）；301是永久跳转
  RedirectView red = new RedirectView("/", true);

  if(code == 301){
   red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
  }
  return red;*/
  session.setAttribute("msg", "jump from redrect");
  return "redirect:/";

 }

 @RequestMapping("/admin")
 @ResponseBody
 public String admin(@RequestParam(value = "key", required = false) String key) {
  if("admin".equals(key)){
   return "hello admin";
  }
  throw new IllegalArgumentException("key 错误");
 }
 //自己定义ExceptionHandler，错误自己写
@ExceptionHandler()
 @ResponseBody
 public String error(Exception e){
 // return  e.getMessage();
  return "error:" + e.getMessage();
 }
}
