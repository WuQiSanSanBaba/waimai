package com.wangqi.dao.impl;

import com.wangqi.dao.AdminDao;
import com.wangqi.entity.Message;
import com.wangqi.entity.PageCondition;
import com.wangqi.entity.PageResult;
import com.wangqi.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AdminDaoImpl implements AdminDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 添加商铺
     *
     * @param store 传入商铺数据用于添加数据
     * @return 返回整形数据 大于0成功，反之报错，这里我在controller采用try catch捕捉，故不需要判断返回值。以后所有方法都用try catch判断
     */
    @Override
    public int addStore(Store store) {
        String sql = "insert store(id,name,isonline,addr,description,openhours,imgaddr,ordered,phonenumber,salecount,imgname,owner,username,password) " +
                "values(null,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
        int result = jdbcTemplate.update(sql, preparedStatement -> {
            preparedStatement.setString(1, store.getName());
            preparedStatement.setObject(2, store.getIsonline());
            preparedStatement.setString(3, store.getAddr());
            preparedStatement.setString(4, store.getDescription());
            preparedStatement.setString(5, store.getOpenhours());
            preparedStatement.setString(6, store.getImgaddr());
            preparedStatement.setString(7, store.getOrdered());
            preparedStatement.setString(8, store.getPhonenumber());
            preparedStatement.setString(9, store.getSalecount());
            preparedStatement.setString(10, store.getImgname());
            preparedStatement.setString(11, store.getOwner());
            preparedStatement.setString(12, store.getUsername());
            preparedStatement.setString(13, store.getPassword());

        });
        return result;
    }

    /**
     * 获取所有的消息
     * @return
     */
    @Override
    public List<Message> getMessage() {
        String sql = "select * from messagecentre";
        List<Message> messageList = jdbcTemplate.query(sql, new RowMapper<Message>() {
            @Override
            public Message mapRow(ResultSet resultSet, int i) throws SQLException {
                Message m = new Message();
                m.setMessage(resultSet.getString("message"));
                m.setId(resultSet.getInt("id"));
                m.setHide(resultSet.getInt("hide"));
                return m;
            }
        });
        return messageList;
    }

    /**
     * 更改消息是隐藏还是显示
     * @param status 传入修改的状态 1是展示 2是隐藏
     * @param id
     */
    @Override
    public void messageChange(Integer status, Integer id) {
        String sql = "update messagecentre set hide=" + status + " where id=" + id;
        jdbcTemplate.execute(sql);
    }

    /**
     * 根据id删除消息
     * @param id 要删除消息的id
     */
    @Override
    public void deleteMessaGebyid(Integer id) {
        String sql = "delete from messagecentre where id=" + id;
        jdbcTemplate.execute(sql);
    }

    /**
     * 添加消息
     * @param message 消息内容
     */
    @Override
    public void addMessage(String message) {
        String sql = "insert messagecentre(message) values('" + message + "')";
        jdbcTemplate.execute(sql);
    }

    /**
     *  通过ID获取user
     * @param id 要获取user的id
     * @return
     */
    @Override
    public User getbyIdUser(Integer id) {
        String sql = "select * from user where id=" + id;
        return jdbcTemplate.queryForObject(sql, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setAddr(resultSet.getString("addr"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setPhonenumber(resultSet.getString("phonenumber"));
                user.setBalance(resultSet.getDouble("balance"));
                user.setImgname(resultSet.getString("imgname"));
                user.setImgaddr(resultSet.getString("imgaddr"));
                user.setDoing(resultSet.getInt("doing"));
                return user;
            }
        });
    }

    /**
     * 获取所有的用户列表并分页  采用模糊查询 like
     * @param pg 分页条件  包括 当前页  页面大小 查询条件
     * @return 分页结果 PageResult类
     */
    @Override
    public PageResult findByPageUser(PageCondition pg) {

        //获取封装的内容
        Integer currentPage = pg.getCurrentPage();
        Integer pageSize = pg.getPageSize();
        String query = pg.getQuery();

        String sql1 = "";//统计多少条数据的字符串查询语句
        String sql2 = "";//统计多少条数据的字符串查询语句
        Integer total;//用来记录总共多少条数据
        if (query != null && query.length() > 0) {
            sql1 = "select count(*) from user where name like '%" + query + "%' or username like '%" + query + "%'";
            sql2 = "select * from user where name like '%" + query + "%'or username like '%" + query + "%'  limit " + (0 + (currentPage - 1) * pageSize) + "," + pageSize;
        } else {
            sql1 = "select count(*) from user ";
            sql2 = "select * from user   limit " + (0 + (currentPage - 1) * pageSize) + "," + pageSize;
        }
        total = jdbcTemplate.queryForObject(sql1, Integer.class);
        //实体类封装数据
        RowMapper<User> rm = new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setAddr(resultSet.getString("addr"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setPhonenumber(resultSet.getString("phonenumber"));
                user.setBalance(resultSet.getDouble("balance"));
                user.setImgname(resultSet.getString("imgname"));
                user.setImgaddr(resultSet.getString("imgaddr"));
                user.setDoing(resultSet.getInt("doing"));
                return user;
            }
        };
        List<User> list = jdbcTemplate.query(sql2, rm);
        return new PageResult(total, list);
    }

    /**
     * 编辑user
     * @param user
     */
    @Override
    public void editUser(User user) {
        String sql = "update user set name=?,addr=?,username=?,password=?,phonenumber=?,balance=?,imgname=?,imgaddr=? where id=?";
        jdbcTemplate.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getAddr());
                preparedStatement.setString(3, user.getUsername());
                preparedStatement.setString(4, user.getPassword());
                preparedStatement.setString(5, user.getPhonenumber());
                preparedStatement.setObject(6, user.getBalance());
                preparedStatement.setString(7, user.getImgname());
                preparedStatement.setString(8, user.getImgaddr());
                preparedStatement.setObject(9, user.getId());
            }
        });

    }

    /**
     * 添加用户
     * @param user
     */
    @Override
    public void addUser(User user) {
        String sql = "insert user(name,addr,username,password,phonenumber,imgname,imgaddr,balance) values(?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement) throws SQLException {
                if (user.getName() != null && user.getName().length() > 0) {
                    preparedStatement.setString(1, user.getName());
                } else {
                    preparedStatement.setString(1, "你还未设置昵称");
                }
                preparedStatement.setString(2, user.getAddr());
                preparedStatement.setString(3, user.getUsername());
                preparedStatement.setString(4, user.getPassword());
                preparedStatement.setString(5, user.getPhonenumber());
                preparedStatement.setString(6, user.getImgname());
                preparedStatement.setString(7, user.getImgaddr());
                preparedStatement.setObject(8, user.getBalance());
            }
        });
    }

    /**
     * 根据id删除用户
     * @param id
     */
    @Override
    public void deletebyIdUser(Integer id) {
        String sql = "delete from user where id=" + id;
        jdbcTemplate.execute(sql);

    }

    /**
     * 根据Id给用户充值
     * @param id 充值的用户ID
     * @param recharge 充值的金额
     */
    @Override
    public void recharge(Integer id, Double recharge) {
        String sql = "update user set balance=balance+" + recharge + " where id= " + id;
        jdbcTemplate.execute(sql);
    }


    /**
     * 分页查询---查询店铺列表 分页公式为  （0+（当前页面-1））*页面大小 ， 页面大小
     * 该方法集成了 分页查询 和 条件查询
     *
     * @param pg 查询条件封装类 包含 当前页面 页面大小  查询条件在owner和name中采用模糊查询
     * @return
     */
    @Override
    public PageResult findByPageStore(PageCondition pg) {

        //获取封装的内容
        Integer currentPage = pg.getCurrentPage();
        Integer pageSize = pg.getPageSize();
        String query = pg.getQuery();

        String sql1 = "";//统计多少条数据的字符串查询语句
        String sql2 = "";//统计多少条数据的字符串查询语句
        Integer total;//用来记录总共多少条数据
        if (query != null && query.length() > 0) {
            sql1 = "select count(*) from store where name like '%" + query + "%' or owner like '%" + query + "%'";
            sql2 = "select * from store where name like '%" + query + "%'or owner like '%" + query + "%' order by ordered asc limit " + (0 + (currentPage - 1) * pageSize) + "," + pageSize;
        } else {
            sql1 = "select count(*) from store ";
            sql2 = "select * from store  order by ordered asc  limit " + (0 + (currentPage - 1) * pageSize) + "," + pageSize;
        }
        total = jdbcTemplate.queryForObject(sql1, Integer.class);
        //实体类封装数据
        RowMapper<Store> rm = new RowMapper<Store>() {
            @Override
            public Store mapRow(ResultSet rs, int rowNum) throws SQLException {
                Store temp = new Store();
                temp.setId(rs.getInt("id"));
                temp.setName(rs.getString("name"));
                temp.setIsonline(rs.getInt("isonline"));
                temp.setAddr(rs.getString("addr"));
                temp.setDescription(rs.getString("description"));
                temp.setOpenhours(rs.getString("openhours"));
                temp.setImgname(rs.getString("imgname"));
                temp.setImgaddr(rs.getString("imgaddr"));
                temp.setOrdered(rs.getString("ordered"));
                temp.setPhonenumber(rs.getString("phonenumber"));
                temp.setSalecount(rs.getString("salecount"));
                temp.setOwner(rs.getString("owner"));
                temp.setUsername(rs.getString("username"));
                temp.setPassword(rs.getString("password"));
                temp.setTurnover(rs.getDouble("turnover"));

                return temp;
            }
        };
        List<Store> list = jdbcTemplate.query(sql2, rm);
        return new PageResult(total, list);
    }

    /**
     * 管理员登录
     * @param admin 封装了username和password
     * @return
     */
    @Override
    public Integer login(Admin admin) {
        String sql = "select count(*) from admin where username='" + admin.getUsername() + "'and password='" + admin.getPassword() + "'";
        Integer integer = jdbcTemplate.queryForObject(sql, Integer.class);
        return integer;
    }

    /**
     * 根据ID查询店铺 对应页面编辑时候回显数据
     * @param id 需要查询的店铺的id
     * @return
     */
    @Override
    public Store getbyIdStore(Integer id) {
        String sql = "select * from store where id= " + id;
        Store store = jdbcTemplate.queryForObject(sql, new RowMapper<Store>() {
            @Override
            public Store mapRow(ResultSet rs, int i) throws SQLException {
                Store temp = new Store();
                temp.setId(rs.getInt("id"));
                temp.setName(rs.getString("name"));
                temp.setIsonline(rs.getInt("isonline"));
                temp.setAddr(rs.getString("addr"));
                temp.setDescription(rs.getString("description"));
                temp.setOpenhours(rs.getString("openhours"));
                temp.setImgname(rs.getString("imgname"));
                temp.setImgaddr(rs.getString("imgaddr"));
                temp.setOrdered(rs.getString("ordered"));
                temp.setPhonenumber(rs.getString("phonenumber"));
                temp.setSalecount(rs.getString("salecount"));
                temp.setOwner(rs.getString("owner"));
                temp.setUsername(rs.getString("username"));
                temp.setPassword(rs.getString("password"));
                temp.setTurnover(rs.getDouble("turnover"));
                return temp;
            }
        });
        return store;
    }

    /**
     * 根据网页发来的数据封装成store类，更改信息
     * @param store
     */
    @Override
    public void editStore(Store store) {

        String sql = "update store set name=?,isonline=?,addr=?,description=?,openhours=?,imgaddr=?,ordered=?,phonenumber=?,salecount=?," +
                "imgname=?,owner=?,username=?,password=? where id=?";
        int update = jdbcTemplate.update(sql, preparedStatement -> {
            preparedStatement.setString(1, store.getName());
            preparedStatement.setObject(2, store.getIsonline());
            preparedStatement.setString(3, store.getAddr());
            preparedStatement.setString(4, store.getDescription());
            preparedStatement.setString(5, store.getOpenhours());
            preparedStatement.setString(6, store.getImgaddr());
            preparedStatement.setString(7, store.getOrdered());
            preparedStatement.setString(8, store.getPhonenumber());
            preparedStatement.setString(9, store.getSalecount());
            preparedStatement.setString(10, store.getImgname());
            preparedStatement.setString(11, store.getOwner());
            preparedStatement.setString(12, store.getUsername());
            preparedStatement.setString(13, store.getPassword());
            preparedStatement.setObject(14, store.getId());


        });
    }

    /**
    *  通过店铺id删除店铺
     * @param id
     */
    @Override
    public void deletebyIdStore(Integer id) {
        String sql ="delete from store where id="+id;
        String sql1="delete from goods_store where store_id="+id;
        jdbcTemplate.execute(sql1);
        jdbcTemplate.execute(sql);
    }

    //更改平台在线状态
    @Override
    public void changeOline(Integer state) {
        String sql = "update admin set isonline=" + state;
        jdbcTemplate.execute(sql);
    }

    //更改商铺在线状态
    @Override
    public void storeChangeOnline(Integer state, Integer id) {
        String sql = "update store set isonline = " + state + " where id= " + id;
        jdbcTemplate.execute(sql);
    }


    //查询平台是否在线
    @Override
    public Integer getIsOnlne() {
        String sql = "select isonline from admin ";
        Integer integer = jdbcTemplate.queryForObject(sql, Integer.class);
        return integer;
    }


}
