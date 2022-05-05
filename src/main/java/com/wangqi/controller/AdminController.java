package com.wangqi.controller;

import com.wangqi.entity.Message;
import com.wangqi.entity.PageCondition;
import com.wangqi.entity.PageResult;
import com.wangqi.entity.Result;
import com.wangqi.pojo.*;
import com.wangqi.service.*;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminController {

    //注入管理员服务
    @Autowired
    AdminService adminService;
    //注入商家服务
    @Autowired
    StoreService storeService;
    //注入用户服务
    @Autowired
    UserService userService;
    //注入通用服务
    @Autowired
    CommonService commonService;
    //注入mongodb操作
    @Autowired
    MongoTemplate mongoTemplate;
    //注入上传工具
    @Autowired
    UpLoadService upLoadService;

    /**
     * 上传图片方法
     *
     * @param imgFile 从网页端接收到后台的图片的名称
     * @return 返回一个结果类，包含成功信息，回显信息，图片在七牛云的地址
     */
    @PostMapping(value = "/upload")
    public Result qiniu(@RequestParam("imgFile") MultipartFile imgFile) throws IOException {
        try {
            String path = upLoadService.upLoadImg(imgFile);
            return new Result(true,"上传成功",path);
        } catch (Exception e) {
                return  new Result(false,"上传失败");
        }
    }

    /**
     * 添加店铺方法
     * insert store()values()
     *
     * @param store 从网页接收到的商店数据，封装为Store类
     * @return 返回一个结果类，包含成功信息，回显信息，图片在七牛云的地址
     */
    @PostMapping(value = "/addstore")
    public Result addStore(@RequestBody Store store) throws IOException {
        try {
            adminService.addStore(store);
            return new Result(true, "添加商户成功");
        } catch (Exception e) {
            return new Result(false, "添加商户失败");
        }
    }

    /**
     * 添加用户方法 从网页接收到user信息封装为user类
     * @param user
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/adduser")
    public Result addUser(@RequestBody User user) throws IOException {
        try {
            adminService.addUser(user);
            return new Result(true, "添加用户成功");
        } catch (Exception e) {
            return new Result(false, "添加用户失败");
        }
    }

    /**
     * 登录验证，用传进来的用户名验证码和数据库比对，成功返回一个大于0的数
     * select count(*) from admin where username='"+admin.getUsername()+"'and password='"+admin.getPassword()+"'"
     *
     * @param admin   接收收到的用户名和密码数据封装为一个Admin类
     * @param request
     * @return
     */
    @PostMapping(value = "/login")
    public Result adminLogin(@RequestBody Admin admin, HttpServletRequest request) {
        Integer login = adminService.login(admin);
        if (login > 0) {
            HttpSession session = request.getSession();
            session.setAttribute("admin", admin);
            return new Result(true, "登录成功");
        } else {
            return new Result(false, "登录失败");
        }
    }

    /**
     * 退出管理员登录
     *
     * @param session 获取session对象以清除Attribute中存在的Admin和Store id
     * @return
     */
    @DeleteMapping("/quitadminlogin")
    public Result quitAdminLogin(HttpSession session) {
        session.removeAttribute("admin");
        session.removeAttribute("storeid");
        return new Result(true, "已经成功退出管理员");
    }

    /**
     * 分页方法，根据传来的 当前页 页面大小 查询条件 进行分页查询
     *
     * @param pg 分页条件实体，包含当前页currentPage 页面大小pageSize 查询条件 query
     * @return
     */
    @PostMapping(value = "/findbypagestore")
    public Result findbyPageStore(@RequestBody PageCondition pg) {
        try {
            PageResult byPage = adminService.findByPageStore(pg);
            return new Result(true, "查询成功", byPage);
        } catch (Exception e) {
            return new Result(false, "查询失败");
        }
    }

    /**
     * 根据获取的商店ID获取商店的所有信息
     *
     * @param id      商店id
     * @param session 用来获取session对象 存储商店ID 以用来查询对应的商品
     * @return
     */
    @GetMapping(value = "/getbyidstore/{id}")
    public Result getbyIdStore(@PathVariable Integer id, HttpSession session) {
        try {
            Store store = adminService.getbyIdStore(id);
            session.setAttribute("store_admin", store);
            return new Result(true, "查询成功", store);
        } catch (Exception e) {
            return new Result(false, "查询失败");
        }
    }

    /**
     * 通过id从数据库获取user信息
     * @param id
     * @return
     */
    @GetMapping(value = "/getbyiduser/{id}")
    public Result getByIdUser(@PathVariable Integer id) {
        try {
            User user = adminService.getbyIdUser(id);

            return new Result(true, "查询成功", user);
        } catch (Exception e) {
            return new Result(false, "查询失败");
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

            Store store = (Store) session.getAttribute("store_admin");
            PageResult pageResult = storeService.findbyPageGoods(cg, store.getId());
            return new Result(true, "查询商品成功", pageResult);
        } catch (Exception e) {
            return new Result(false, "查询商品失败");
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
            Store store = (Store) session.getAttribute("store_admin");
            storeService.addGoods(goods, store.getId());
            return new Result(true, "添加成功");
        } catch (Exception e) {
            return new Result(false, "添加失败");
        }
    }


    /**
     * //根据传过来的JSON数据封装对象，用于编辑商铺信息
     *
     * @param store Store类，用来update
     * @return
     */
    @PutMapping(value = "/editstore")
    public Result editStore(@RequestBody Store store) {
        try {
            adminService.editStore(store);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            return new Result(false, "修改失败");
        }
    }

    @PutMapping(value = "/edituser")
    public Result editUser(@RequestBody User user) {
        try {
            adminService.editUser(user);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            return new Result(false, "修改失败");
        }
    }

    /**
     * 删除店铺
     *
     * @param id  对应商店ID  delete from store where id = id
     * @param img
     * @return
     */
    @DeleteMapping(value = "deletebyidstore/{id}/{img}")
    public Result deletebyIdStore(@PathVariable Integer id, @PathVariable String img) {
        try {
            //删除数据库的商店信息
            adminService.deletebyIdStore(id);
            if (img != null) {
                //删除七牛云的图片
            }
            return new Result(true, "删除成功");
        } catch (Exception e) {
            return new Result(false, "删除失败");
        }

    }
    /**
     * 修改平台上线状态
     * update admin set isonline="+state
     *
     * @param state
     * @return
     */
    @PutMapping(value = "/changeonline/{state}")
    public Result changeOnline(@PathVariable Integer state) {
        try {
            String message = "";
            adminService.changeOline(state);
            if (state == 1) {
                message = "已经上线";
            } else {
                message = "已经下线";
            }
            return new Result(true, "更改成功," + message);
        } catch (Exception e) {
            return new Result(false, "更改失败");
        }
    }

    /**
     * 修改店铺上线状态
     * update store set isonline = "+state+" where id= "+id
     *
     * @param state
     * @param id
     * @return
     */
    @PutMapping(value = "/storechangeonline/{state}/{id}")
    public Result storeChangeOnline(@PathVariable Integer state, @PathVariable Integer id) {
        try {
            adminService.storeChangeOnline(state, id);
            String message = "";
            if (state == 1) {
                message = "已经上线";
            } else {
                message = "已经下线";
            }
            return new Result(true, "更改成功," + message);
        } catch (Exception e) {
            return new Result(false, "更改失败");
        }
    }

    /**
     * 获取平台上线状态
     * "select isonline from admin "
     *
     * @return
     */
    @GetMapping(value = "/getisonlne")
    public Result getIsOnlne() {
        try {
            Integer isonlne = adminService.getIsOnlne();
            return new Result(true, "获取状态成功", isonlne);
        } catch (Exception e) {
            return new Result(false, "获取状态失败");
        }
    }

    /**
     * 获取平台的所有消息内容
     * select * from messagecentre
     *
     * @return
     */
    @GetMapping(value = "/getmessage")
    public Result getMessage() {
        try {
            List<Message> messageList = adminService.getMessage();
            return new Result(true, "获取状态成功", messageList);
        } catch (Exception e) {
            return new Result(false, "获取状态失败");
        }
    }

    /**
     * 修改消息隐藏还是展示
     * update messagecentre set hide="+status+" where id="+id
     *
     * @param status 从网页接收到的修改消息的状态 1对应开启消息 0对应关闭消息
     * @param id     消息的id
     * @return
     */
    @PutMapping(value = "/messagechange/{status}/{id}")
    public Result messageChange(@PathVariable Integer status, @PathVariable Integer id) {
        try {
            adminService.messageChange(status, id);

            return new Result(true, "修改状态成功");
        } catch (Exception e) {
            return new Result(false, "修改状态失败");
        }

    }

    /**
     * 根据id删除消息
     * delete from messagecentre where id="+id;
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/deletemessagebyid/{id}")
    public Result deleteMessaGebyid(@PathVariable Integer id) {
        try {
            adminService.deleteMessaGebyid(id);
            return new Result(true, "删除消息成功");
        } catch (Exception e) {
            return new Result(false, "删除消息失败");
        }
    }

    /**
     * 添加消息
     * insert messagecentre(message) values('"+message+"')"
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/addmessage/{message}")
    public Result addMessage(@PathVariable String message) {
        try {
            adminService.addMessage(message);
            return new Result(true, "添加消息成功");
        } catch (Exception e) {
            return new Result(false, "添加消息失败");
        }
    }

    /**
     * 获取所有用户并分页
     * @param pg 分页条件 当前页 页面大小 查询条件
     * @return
     */
    @PostMapping(value = "findbypageuser")
    public Result findByPageUser(@RequestBody PageCondition pg) {
        try {
            PageResult ps = adminService.findByPageUser(pg);
            return new Result(true, "获取信息成功", ps);
        } catch (Exception e) {
            return new Result(false, "获取信息失败");
        }
    }

    /**
     * 给用户充钱业务
     * @param id 用户ID
     * @param recharge 充多少钱
     * @return
     */
    @PostMapping("/recharge/{id}/{recharge}")
    public Result recharge(@PathVariable Integer id, @PathVariable Double recharge) {
        try {
            adminService.recharge(id, recharge);
            return new Result(true, "充值成功");
        } catch (Exception e) {
            return new Result(false, "充值失败");
        }

    }

    /**
     * 删除订单  并通过orderid获取到order中的userid和total,把钱退给用户
     * @param orderid
     * @return
     */
    @DeleteMapping("/deleteorder/{orderid}")
    public Result deleteOrder(@PathVariable String orderid) {
        try {
            Order order = mongoTemplate.findOne(new Query(Criteria.where("_id").is(orderid)), Order.class, "order");
            commonService.returnBalance(order.getUserId(), order.getTotal());
            mongoTemplate.remove(new Query(Criteria.where("_id").is(orderid)), "order");
            return new Result(true, "订单已经删除");
        } catch (Exception e) {
            return new Result(false, "服务器故障，请稍后重试");
        }

    }

    /**
     * 完成订单   并给商家营业额+total 和 销售量+1
     * @param orderid
     * @return
     */
    @PostMapping("/finishorder/{orderid}")
    public Result finishOrder(@PathVariable String orderid) {
        try {
            Order order = mongoTemplate.findOne(new Query(Criteria.where("_id").is(orderid)), Order.class, "order");
            storeService.addRurnoverAndSaleCount(order.getStoreId(), order.getTotal());
            mongoTemplate.remove(new Query(Criteria.where("_id").is(orderid)), "order");

            return new Result(true, "订单已经完成");
        } catch (Exception e) {
            return new Result(false, "服务器故障，请稍后重试");
        }

    }

    /**
     * 对应管理员页面的订单管理 获取到所有的订单信息
     * @return
     */
    @GetMapping("/getorder")
    public Result getOrder() {
        try {

            List<Order> list = mongoTemplate.findAll(Order.class, "order");
            if (list == null) {
                return new Result(false, "没有订单");
            }
            for (Order order : list) {
                String orderId = order.getId();
                List<Bill> bills = mongoTemplate.find(new Query().addCriteria(Criteria.where("orderId").is(orderId)), Bill.class, "bill");
                order.setBill(bills);
            }
            return new Result(true, "", list);
        } catch (Exception e) {
            return new Result(false, "服务器故障");
        }

    }
}
