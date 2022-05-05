package com.wangqi.dao.impl;

import com.wangqi.dao.StoreDao;
import com.wangqi.entity.PageCondition;
import com.wangqi.entity.PageResult;
import com.wangqi.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StoreDaoImpl implements StoreDao {
    @Autowired
    JdbcTemplate jdbcTemplate;
    //登录验证，查询数据库，有数据就登录成功，没有数据就是登录失败
    @Override
    public Store login(Store store) {
        String sql1="select * from store where username='"+store.getUsername()+"'and password='"+store.getPassword()+"'";
        Store resultstore = null;
        try {
            resultstore = jdbcTemplate.queryForObject(sql1, new RowMapper<Store>() {
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
                    temp.setUsername(rs.getString("owner"));
                    temp.setUsername(rs.getString("username"));
                    temp.setPassword(rs.getString("password"));
                    temp.setTurnover(rs.getDouble("turnover"));
                    return temp;
                }
            });
        } catch (DataAccessException e) {
            return null;
        }
        return resultstore;
    }

    /**
     *
     * @param id  为店铺ID直接获取所有数据
     * @return
     */
    @Override
    public Store getStoreInfo(Integer id) {
        Store store = null;
        try {
            store = jdbcTemplate.queryForObject("select * from store where id= "+id, new RowMapper<Store>() {
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
                    temp.setTurnover(rs.getDouble("turnover"));
                    return temp;
                }
            });
        } catch (DataAccessException e) {
            return null;
        }
        return store;
    }
    //与admindao里边的editstore一样
    @Override
    public void editStore(Store store) {
        String sql="update store set name=?,isonline=?,addr=?,description=?,openhours=?,imgaddr=?,ordered=?,phonenumber=?,salecount=?,imgname=? where id=?";
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
            preparedStatement.setObject(11, store.getId());
        });
    }


    //商品查询并分页1.先用商户ID去中间表查对应的商品ID数组 2.用商品ID数组查询所有的商品
    @Override
    public PageResult findbyPageGoods(PageCondition cg, Integer id) {
        //获取 分页条件 和 查询餐宿
        Integer pageSize = cg.getPageSize();
        Integer currentPage = cg.getCurrentPage();
        String query = cg.getQuery();

        //1.先用商户ID去中间表查对应的商品ID数组
        String sql="select goods_id from goods_store where store_id ="+id;
        List<Integer> goods_idlist = jdbcTemplate.query(sql, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt("goods_id");
            }
        });
        //2.用商品ID数组查询所有的商品
        List<Goods> goodsList=new ArrayList<>();
        for (Integer integer : goods_idlist) {
            String sqlforgoods="";
            if (query!=null&&query.length()>0){
                 sqlforgoods="select * from goods where id="+integer+" and name like '%"+query+"%'";
            }else {
                 sqlforgoods="select * from goods where id="+integer;
            }
            Goods goods = null;
            try {
                goods = jdbcTemplate.queryForObject(sqlforgoods, new RowMapper<Goods>() {
                     @Override
                     public Goods mapRow(ResultSet resultSet, int i) throws SQLException {
                         Goods tem = new Goods();
                         tem.setId(resultSet.getInt("id"));
                         tem.setName(resultSet.getString("name"));
                         tem.setType(resultSet.getString("type"));
                         tem.setCount(resultSet.getInt("count"));
                         tem.setPrice(resultSet.getDouble("price"));
                         tem.setIson(resultSet.getInt("ison"));
                         tem.setImgaddr(resultSet.getString("imgaddr"));
                         tem.setImgname(resultSet.getString("imgname"));
                         tem.setDescription(resultSet.getString("description"));
                         return tem;
                     }
                 });
            } catch (DataAccessException e) {

            }
            if (goods!=null) {
                goodsList.add(goods);
            }
        }
        Integer total=0;
        List<Goods> pageList=null;
        //对集合进行分页
        if (goodsList!=null&&goodsList.size()>0){
            total=goodsList.size();
            Integer begin=(currentPage-1)*pageSize;
           Integer end= pageSize*currentPage;
           if (end>total){
               end=total;
           }
             pageList = goodsList.subList(begin,end );
        }

        PageResult pageResult=new PageResult(total,pageList);
        return pageResult;
    }
    //获取所有的商品
    @Override
    public List<Goods> getAllGoods( Integer id) {
        //1.先用商户ID去中间表查对应的商品ID数组
        String sql="select goods_id from goods_store where store_id ="+id;
        List<Integer> goods_idlist = jdbcTemplate.query(sql, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt("goods_id");
            }
        });
        //2.用商品ID数组查询所有的商品
        List<Goods> goodsList=new ArrayList<>();
        for (Integer integer : goods_idlist) {
            String sqlforgoods="";
            sqlforgoods="select * from goods where id="+integer;
            Goods goods = null;
            try {
                goods = jdbcTemplate.queryForObject(sqlforgoods, new RowMapper<Goods>() {
                    @Override
                    public Goods mapRow(ResultSet resultSet, int i) throws SQLException {
                        Goods tem = new Goods();
                        tem.setId(resultSet.getInt("id"));
                        tem.setName(resultSet.getString("name"));
                        tem.setType(resultSet.getString("type"));
                        tem.setCount(resultSet.getInt("count"));
                        tem.setPrice(resultSet.getDouble("price"));
                        tem.setIson(resultSet.getInt("ison"));
                        tem.setImgaddr(resultSet.getString("imgaddr"));
                        tem.setImgname(resultSet.getString("imgname"));
                        tem.setDescription(resultSet.getString("description"));
                        return tem;
                    }
                });
            } catch (DataAccessException e) {

            }
            if (goods!=null) {
                goodsList.add(goods);
            }
        }
        return goodsList;
    }

    /**
     * 修改店铺是否上线
     * @param state 1代表上线 0代表下线
     * @param id 商店的id
     */
    @Override
    public void goodSison(Integer state, Integer id) {
        String sql="update goods set ison="+state+" where id ="+id;
        jdbcTemplate.execute(sql);
    }

    /**
     * 通过封装goods获取所有信息，更新商品信息
     * @param goods 封装goods类
     */
    @Override
    public void editGoods(Goods goods) {
        String sql="update goods set name=?,type=?,count=?,price=?,ison=?,imgaddr=?,imgname=?,description=? where id=?";
        jdbcTemplate.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1,goods.getName());
                preparedStatement.setString(2,goods.getType());
                preparedStatement.setObject(3,goods.getCount());
                preparedStatement.setObject(4,goods.getPrice());
                preparedStatement.setObject(5,goods.getIson());
                preparedStatement.setString(6,goods.getImgaddr());
                preparedStatement.setString(7,goods.getImgname());
                preparedStatement.setString(8,goods.getDescription());
                preparedStatement.setObject(9,goods.getId());
            }
        });
    }

    /**
     * 根据商品id获取商品
     * @param id
     * @return
     */
    @Override
    public Goods getbyIdGoods(Integer id) {
        String sql="select * from goods where id="+id;
        Goods goods = jdbcTemplate.queryForObject(sql, new RowMapper<Goods>() {
            @Override
            public Goods mapRow(ResultSet resultSet, int i) throws SQLException {
                Goods tem = new Goods();
                tem.setId(resultSet.getInt("id"));
                tem.setName(resultSet.getString("name"));
                tem.setType(resultSet.getString("type"));
                tem.setCount(resultSet.getInt("count") );
                tem.setPrice(resultSet.getDouble("price"));
                tem.setIson(resultSet.getInt("ison"));
                tem.setImgaddr(resultSet.getString("imgaddr"));
                tem.setImgname(resultSet.getString("imgname"));
                tem.setDescription(resultSet.getString("description"));
                return tem;
            }
        });
        return goods;
    }

    /**
     *  从goods类获取数据添加商品 并插入中间表对应的商品和商店中间表的ID
     * @param goods 商品类
     * @param id  商店id 中间表用
     */
    @Override
    public void addGoods(Goods goods,Integer id) {
        String sql1="insert goods(name,type,count,price,ison,imgaddr,imgname,description) values(?,?,?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, goods.getName());
                ps.setString(2, goods.getType());
                ps.setObject(3, goods.getCount());
                ps.setObject(4, goods.getPrice());
                ps.setObject(5, goods.getIson());
                ps.setString(6, goods.getImgaddr());
                ps.setString(7, goods.getImgname());
                ps.setString(8, goods.getDescription());
                return ps;
            }
        },keyHolder);
        Integer goodsid=keyHolder.getKey().intValue();
        //插入到中间表
        String sql2="insert goods_store(store_id,goods_id) values("+id+","+goodsid+")";
        jdbcTemplate.execute(sql2);
    }

    /**
     * 通过ID删除商品数据
     * @param id
     */
    @Override
    public void deletebyIdGoods(Integer id) {
        String sql1="delete from goods_store where goods_id ="+id;
        jdbcTemplate.execute(sql1);
        String sql2="delete from goods where id="+id;
    }

    /**
     * 完成订单时候 销售额+total 销售量+1
     * @param storeId
     * @param total
     */
    @Override
    public void addRurnoverAndSaleCount(int storeId, double total) {
        String sql="update store set salecount=salecount+1,turnover=turnover+"+total+" where id="+storeId;
        jdbcTemplate.execute(sql);
    }


}
