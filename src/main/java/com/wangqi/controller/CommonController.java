package com.wangqi.controller;

import com.wangqi.entity.PayResult;
import com.wangqi.entity.Result;
import com.wangqi.pojo.Bill;
import com.wangqi.pojo.Order;
import com.wangqi.pojo.Store;
import com.wangqi.pojo.User;
import com.wangqi.service.CommonService;
import com.wangqi.service.StoreService;
import com.wangqi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Autowired
    CommonService commonService;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    StoreService storeService;
    @Autowired
    UserService userService;
    /**
     * 获取消息中心的所有消息 不包括hide 和 id 属性 同管理员页面的不同
     * select message from messagecentre where hide >0
     *
     * @return
     */
    @GetMapping("/getmessage")
    public Result getMessage() {
        try {
            List<String> list = commonService.getMessage();
            //封装成json形式
            List<Map<String, String>> listmp = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Map<String, String> mp = new HashMap<>();
                mp.put("message", list.get(i));
                listmp.add(mp);
            }
            return new Result(true, "成功", listmp);
        } catch (Exception e) {
            return new Result(false, "没有消息");
        }

    }

    /**
     *
     * @return 返回count个数据给走马灯显示，这几个数据与store中的ordered有关，从ordered最下的开始显示
     */
    @GetMapping("/initcarousel")
    public Result initCarousel(){
        try {
            int count=4;
            List<Store> list=  commonService.initCarousel(count);
            return new Result(true,"成功",list);
        } catch (Exception e) {
            return new Result(false,"失败");
        }
    }
    /**
     *
     * @return 返回count个数据给走马灯显示，这几个数据与store中的ordered有关，从ordered最下的开始显示
     */
    @GetMapping("/getallstore")
    public Result getAllStore(){
        try {

            List<Store> list=  commonService.getAllStore();
            return new Result(true,"成功",list);
        } catch (Exception e) {
            return new Result(false,"失败");
        }
    }


    @PostMapping("/payBills")
    public Result sendBill(@RequestBody PayResult payResult, HttpSession session){
        try {
            User userOld = (User) session.getAttribute("user");
            //先判断有没有登录
            if (userOld==null){
                return new Result(false,"您还没有登录");
            }
            //重新请求user数据再从用户余额里扣钱
            User user = userService.login(userOld);
            boolean b=    commonService.reduceMoneyAndCount(user,payResult.getTotal());

            if (user.getBalance()>=payResult.getTotal()) {
                //把所有账单塞进mongdb的bill集合和扣商家库存
                for (Bill bill : payResult.getList()) {
                    mongoTemplate.save(bill,"bill");
                    commonService.reduceCount(bill.getGoodsId(),bill.getNumber());
                }

                Order order=new Order();
                order.setId(payResult.getOrderId());
                order.setStoreId(payResult.getStore().getId());
                order.setUserId(payResult.getUser().getId());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                order.setDate(sdf.format(new Date()));
                order.setAddr(payResult.getUser().getAddr());
                order.setPhonenumber(user.getPhonenumber());
                order.setUserName(user.getName());
                order.setStoreName(payResult.getStore().getName());
                order.setTotal(payResult.getTotal());
                order.setStatus("已经下单");//已经下单，正在配送，配送完成
                mongoTemplate.save(order,"order");

                return new Result(true,"支付成功，马上给您配送，请稍等");
            } else {
                return new Result(false,"您的余额不足，请及时充值");
            }
        } catch (Exception e) {
            return new Result(false,"服务器异常，请稍后重试");
        }
    }
    @GetMapping("/init/{which}")
    public Result init(@PathVariable String which,HttpSession session){
        try {
            User user = (User) session.getAttribute("user");
            Store store = (Store) session.getAttribute("store_browsing");
            switch (which){
                case "store": return new Result(true,"初始化商店成功",store);
                case "goods": return new Result(true,"初始化商品成功",storeService.getAllGoods(store.getId()));
                case "user":
                    if (user!=null) {
                        return new Result(true,"初始化用户成功",user);
                    } else {
                        return new Result(false,"您还没有登录哦");
                    }
            }
            return new Result(false,"服务器故障");
        } catch (Exception e) {
            return new Result(false,"服务器故障");
        }
    }
    @PostMapping("browsestore")
    public Result browseStore(@RequestBody Store store_browsing,HttpSession session){
        try {
            session.setAttribute("store_browsing",store_browsing);
            return new Result(true,"");
        } catch (Exception e) {
            return new Result(false,"服务器故障");
        }

    }
    /**
     * 删除订单 并把钱退给用户
     * @param orderid 通过orderid在mongodb查询order,
     * @return
     */
    @DeleteMapping("/deleteorder/{orderid}")
    public Result deleteOrder(@PathVariable String orderid){
        try {

            Order order = mongoTemplate.findOne(new Query(Criteria.where("_id").is(orderid)), Order.class, "order");
            commonService.returnBalance(order.getUserId(),order.getTotal());
            mongoTemplate.remove(new Query(Criteria.where("_id").is(orderid)), "order");
            return new Result(true,"订单已经删除,退款稍后返回您的账户");
        } catch (Exception e) {
            return new Result(false,"服务器故障，请稍后重试");
        }

    }
}
