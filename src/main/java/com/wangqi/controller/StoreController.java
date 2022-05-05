package com.wangqi.controller;

import com.mongodb.client.MongoCollection;
import com.wangqi.entity.PageCondition;
import com.wangqi.entity.PageResult;
import com.wangqi.entity.Result;
import com.wangqi.pojo.*;
import com.wangqi.service.StoreService;
import com.wangqi.service.UpLoadService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/store")
public class StoreController {
    //全局变量
    //注入七牛云服务
    @Autowired
    UpLoadService upLoadService;
    //注入商家服务
    @Autowired
    StoreService service;
    @Autowired
    MongoTemplate mongoTemplate;

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
     * 商店登录验证 并获取对应的ID
     * select id from store where username='"+store.getUsername()+"'and password='"+store.getPassword()+"'
     *
     * @param store   封装用户名密码
     * @param session 用来获取session域，并将获取到的ID设置到Attribute里
     * @return
     */
    @PostMapping(value = "/login")
    public Result login(@RequestBody Store store, HttpSession session) {
        try {
            Store storeresult = service.login(store);
            if (storeresult != null) {
                session.setAttribute("store_storeowner", storeresult);
                System.out.println(storeresult.getId()+"正在登录");
                return new Result(true, "登录成功,即将跳转到商家主页");
            }
        } catch (Exception e) {
            new Result(false, "登录失败,请检查账号密码");
        }
        return new Result(false, "登录失败,请检查账号密码");

    }

    /**
     * 退出登录
     * 不需要查询数据库，直接清除session中的store
     *
     * @param session
     * @return
     */
    @GetMapping(value = "/quitlogin")
    public Result quitLogin(HttpSession session) {
        try {
            session.removeAttribute("store_storeowner");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "成功退出");
        }
        return new Result(true, "成功退出");
    }

    /**
     * 获取商户首页信息
     * select * from goods where id="+id
     *
     * @param session 获取session，通过它得到store 进而进行查询
     * @return
     */
    @GetMapping(value = "/getstoreinfo")
    public Result getimgBySession(HttpSession session) {
        try {

            Store store = (Store) session.getAttribute("store_storeowner");

            if (store == null) {
                return new Result(false, "还没有登录");
            } else {
                return new Result(true, "获取信息成功", store);
            }
        } catch (Exception e) {
            return new Result(false, "获取信息失败，请联系管理员");
        }

    }

    /**
     * 商家端的修改店铺
     * update store set name=?,isonline=?,addr=?,description=?,openhours=?,imgaddr=?,ordered=?,phonenumber=?,salecount=?,imgname=? where id=?
     *
     * @param store 传进来的商铺信息，这个与管理员的不同是，不提供排名orered的修改
     * @return
     */
    @PutMapping(value = "/editstore")
    public Result editStore(@RequestBody Store store) {
        try {
            service.editStore(store);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            return new Result(false, "修改失败");
        }

    }

    /**
     * 查询所有的商品
     * 语句对应StoreDaoImpl 同名方法不赘述了
     *
     * @param cg      分页条件封装类，当前页 页面大小 查询条件
     * @param session 用来获取session，并通过它获取store
     * @return
     */
    @PostMapping(value = "/findbypagegoods")
    public Result findbyPageGoods(@RequestBody PageCondition cg, HttpSession session) {
        try {

            Store store = (Store) session.getAttribute("store_storeowner");
            PageResult pageResult = service.findbyPageGoods(cg, store.getId());
            return new Result(true, "查询商品成功", pageResult);
        } catch (Exception e) {
            return new Result(false, "查询商品失败");
        }

    }



    /**
     * 修改商品上架状态
     * update goods set ison="+state+" where id ="+id
     *
     * @param state 需要修改的状态 1是上架 0是下架
     * @param id
     * @return
     */
    @PutMapping(value = "/goodsison/{state}/{id}")
    public Result storeChangeOnline(@PathVariable Integer state, @PathVariable Integer id) {
        System.out.println(state + id);
        try {
            service.changeGoodsIson(state, id);
            String message = "";
            if (state == 1) {
                message = "已经上架";
            } else {
                message = "已经下架";
            }
            return new Result(true, "更改成功," + message);
        } catch (Exception e) {
            return new Result(false, "更改失败");
        }
    }

    /**
     * 修改商品
     * update goods set name=?,type=?,count=?,price=?,ison=?,imgaddr=?,imgname=?,description=? where id=?"
     *
     * @param goods
     * @return
     */
    @PutMapping("/editgoods")
    public Result editGoods(@RequestBody Goods goods) {
        try {
            System.out.println(goods);
            service.editGoods(goods);
            return new Result(true, "更改成功");
        } catch (Exception e) {
            return new Result(false, "更改失败");
        }
    }

    /**
     * 通过ID获取商品信息，以便回显,对应打开编辑窗口，显示的数据
     *
     * @param id 传进来的商品ID用来查询数据库库
     * @return
     */
    @GetMapping("/getbyidgoods/{id}")
    public Result getbyidgoods(@PathVariable Integer id) {
        try {
            Goods goods = service.getbyIdGoods(id);
            return new Result(true, "获取成功", goods);
        } catch (Exception e) {
            return new Result(false, "获取失败");
        }
    }

    /**
     * 添加商品信息
     *
     * @param goods   添加商品的数据
     * @param session 获取session得到store，ID作为where的判断条件
     * @return
     */
    @PostMapping("/addgoods")
    public Result addGoods(@RequestBody Goods goods, HttpSession session) {
        try {
            Store store = (Store) session.getAttribute("store_storeowner");
            service.addGoods(goods, store.getId());
            return new Result(true, "添加成功");
        } catch (Exception e) {
            return new Result(false, "添加失败");
        }
    }

    /**
     * 删除商品信息
     *
     * @param id      需要删除商品的ID
     * @param imgname 得到图片名称用来删除云存储的数据
     * @return
     */
    @DeleteMapping("/deletebyidgoods/{id}/{imgname}")
    public Result deletebyIdGoods(@PathVariable Integer id, @PathVariable String imgname) {
        try {
            if (imgname!=null){
            }
            service.deletebyIdGoods(id);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            return new Result(false, "删除成功");
        }
    }


    @GetMapping("/getorder")
    public Result getOrder(HttpSession session){
        try {
            Store store= (Store) session.getAttribute("store_storeowner");
            Query query = new Query();
            query.addCriteria(Criteria.where("storeId").is(store.getId()));
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
    @PostMapping("/finishorder/{orderid}")
    public Result finishOrder(@PathVariable String orderid){
        try {
            mongoTemplate.remove(new Query(Criteria.where("_id").is(orderid)), "order");
            return new Result(true,"订单已经完成");
        } catch (Exception e) {
            return new Result(false,"服务器故障，请稍后重试");
        }

    }@DeleteMapping("/deleteorder/{orderid}")
    public Result deleteOrder(@PathVariable String orderid){
        try {
            mongoTemplate.remove(new Query(Criteria.where("_id").is(orderid)), "order");
            return new Result(true,"订单已经删除");
        } catch (Exception e) {
            return new Result(false,"服务器故障，请稍后重试");
        }

    }

}
