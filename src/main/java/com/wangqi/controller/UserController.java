package com.wangqi.controller;

import com.wangqi.pojo.Bill;
import com.wangqi.entity.Result;
import com.wangqi.pojo.Order;
import com.wangqi.pojo.User;
import com.wangqi.service.StoreService;
import com.wangqi.service.UpLoadService;
import com.wangqi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    StoreService storeService;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UpLoadService upLoadService;
    /**
     * index.html页面的用户数据初始化 如果有重新查询一次数据库更新信息
     * @param session
     * @return
     */
    @GetMapping("/init")
    public Result init(HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user!=null){
            Query query=new Query(Criteria.where("userId").is(user.getId()));
            User userUpdated = userService.login(user);
            List<Order> list = mongoTemplate.find(query, Order.class, "order");
            userUpdated.setDoing(list.size());
            return  new Result(true,"有信息",userUpdated);
        }else {
            return new Result(false,"您还没有登录");
        }
    }


    /**
     * 上传图片方法
     *
     * @param imgFile 从网页端接收到后台的图片的名称
     * @return 返回一个结果类，包含成功信息，回显信息，图片在七牛云的地址
     */
    @PostMapping(value = "/upload")
    public Result upLoadImage(@RequestParam("imgFile") MultipartFile imgFile) throws IOException {
        try {
            String path = upLoadService.upLoadImg(imgFile);
            return new Result(true,"上传成功",path);
        } catch (Exception e) {
            return  new Result(false,"上传失败");
        }
    }

    /**
     * 用户登录
     * @param userLogin
     * @param session
     * @return
     */
    @PostMapping("/userlogin")
    public Result userLogin(@RequestBody User userLogin, HttpSession session){
        try {
        User user = userService.login(userLogin);
        if (user!=null){
            session.setAttribute("user", user);
            return new Result(true,"登录成功",user);
        }
        else {
            return new Result(false,"登录失败");
        }
        }catch (Exception e){
            return new Result(false,"登录失败");
        }

    }

    /**
     * 退出登录
     * @param session
     * @return
     */
    @DeleteMapping("/quituserlogin")
    public Result quitUserLogin(HttpSession session){
        session.removeAttribute("user");
        return new Result(true,"退出登录成功");
    } /**
     * 注册
     * @param session
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user,HttpSession session){
        try {
          String result=  userService.register(user);
          if ("注册成功".equals(result)){
              user.setBalance(0.00);
              session.setAttribute("user",user);
            return new Result(true,"注册成功");}
          else {
              return new Result(false,"用户名已经存在，请换一个");}
          }
        catch (Exception e) {
            System.out.println("服务器异常");
            return new Result(false,"服务器异常");
        }
    }
    @GetMapping("/getorder")
    public Result getOrder(HttpSession session){
        try {
            User user= (User) session.getAttribute("user");
            if (user==null){
                return  new Result(false,"还没有登录");
            }
            Query query = new Query();
            query.addCriteria(Criteria.where("userId").is(user.getId()));
            List<Order> list = mongoTemplate.find(query, Order.class,"order");
            if (list==null){
                return new Result(false,"您还没有下单");
            }
            for (Order order : list) {
                String orderId = order.getId();
                List<Bill> bills = mongoTemplate.find(new Query().addCriteria(Criteria.where("orderId").is(orderId)), Bill.class, "bill");
                order.setBill(bills);
            }
            return new Result(true,"",list);
        } catch (Exception e) {
            return new Result(false,"服务器故障");
        }


    }

}

