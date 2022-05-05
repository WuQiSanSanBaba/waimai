package com.wangqi.dao.impl;

import com.wangqi.dao.CommonDao;
import com.wangqi.pojo.Store;
import com.wangqi.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CommonDaoImpl implements CommonDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 获取首页的消息列表
     * @return
     */
    @Override
    public List<String> getMessage() {
        String sql="select message from messagecentre where hide >0";

        List<String>  list = jdbcTemplate.query(sql, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {

                return resultSet.getString(1);
            }
        });
        return list;
    }

    /**
     * 获取首页走马灯显示的店铺
     * @param count 显示的走马灯数量
     * @return
     */
    @Override
    public List<Store> initCarousel(int count) {
        String sql="select * from store order by ordered asc  LIMIT "+count;
        List<Store> list = jdbcTemplate.query(sql, new RowMapper<Store>() {
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
                return temp;
            }
        });
        return list;
    }

    /**
     * 获取所有的店铺
     * @return
     */
    @Override
    public List<Store> getAllStore() {
        String sql="select * from store order by ordered asc  ";
        List<Store> list = jdbcTemplate.query(sql, new RowMapper<Store>() {
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
                return temp;
            }
        });
        return list;
    }

    /**
     * 下单时候给用户扣钱
     * @param user 扣钱的用户
     * @param total 扣钱的数额
     * @return
     */
    @Override
    public boolean reduceMoneyAndCount(User user, double total ) {
        String userSql="UPDATE user SET balance=balance-"+total+" WHERE id="+user.getId();
        jdbcTemplate.execute(userSql);
        return false;
    }

    /**
     * 减少商品库存
     * @param goodsId 减少库存的商品ID
     * @param number 减少库存的数量
     */
    @Override
    public void reduceCount(Integer goodsId, int number) {
        String sql="update goods set count=count-"+number+" where id="+goodsId;
        jdbcTemplate.execute(sql);
    }

    /**
     * 给用户退款
     * @param userID
     * @param total
     */
    @Override
    public void returnBalance(Integer userID,Double total) {
        String sql="update user set balance=balance+"+total+" where id= "+userID;
        jdbcTemplate.execute(sql);
    }


}
