package com.wangqi.dao.impl;

import com.wangqi.dao.UserDao;
import com.wangqi.entity.PageCondition;
import com.wangqi.entity.PageResult;
import com.wangqi.pojo.Store;
import com.wangqi.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDaoimpl implements UserDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 用户登录
     * @param userLogin 用user类封装username 和 password
     * @return
     */
    @Override
    public User login(User userLogin) {
        String sql="select * from user where username='"+userLogin.getUsername()+"' and password='"+userLogin.getPassword()+"'";
        User user = jdbcTemplate.queryForObject(sql, new RowMapper<User>() {
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
        return user;
    }

    /**
     * 用户注册  通过封装的user对象获取各项信息插入数据库
     * @param user
     * @return
     */
    @Override
    public String register(User user) {
        try {
            String sql="insert user(name,addr,username,password,phonenumber,imgname,imgaddr) values(?,?,?,?,?,?,?)";
            jdbcTemplate.update(sql, new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement) throws SQLException {
                    if (user.getName()!=null&&user.getName().length()>0) {
                        preparedStatement.setString(1,user.getName());
                    } else {
                        preparedStatement.setString(1,"你还未设置昵称");
                    }
                    preparedStatement.setString(2,user.getAddr());
                    preparedStatement.setString(3,user.getUsername());
                    preparedStatement.setString(4,user.getPassword());
                    preparedStatement.setString(5,user.getPhonenumber());
                    preparedStatement.setString(6,user.getImgname());
                    preparedStatement.setString(7,user.getImgaddr());
                }
            });
            return "注册成功";
        } catch (DataAccessException e) {
            return "用户名已经存在";
        }
    }


}
