package com.xyc.DAOplus;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;

/**
 * 实现类，继承BaseDAO实现CustomerDAO接口，并重写方法
 * @auther xuyuchao
 * @create 2022-03-20-16:28
 */
public class CustomerDAOImpl extends BaseDAO<customer> implements CustomerDAO {

    @Override
    public void insert(Connection con, customer cust) {
        String sql = "insert into customers(name,email,birth) values(?,?,?)";
        update(con,sql,cust.getName(),cust.getEmail(),cust.getBirth());
    }

    @Override
    public void deleteById(Connection con, int id) {
        String sql = "delete from customers where id = ?";
        update(con,sql,id);
    }

    @Override
    public void update(Connection con, customer cust) {
        String sql = "update customers set name = ?,email = ?,birth = ? where id = ?";
        update(con,sql,cust.getName(),cust.getEmail(),cust.getBirth(),cust.getId());
    }

    @Override
    public customer getCustomerById(Connection con, int id) {
        String sql = "select id,name,email,birth from customers where id = ?";
        customer cust = getInstance(con, sql, id);
        return cust;
    }

    @Override
    public List<customer> getAll(Connection con) {
        String sql = "select id,name,email,birth from customers";
        List<customer> list = getListInstance(con,sql);
        return list;
    }

    @Override
    public Long getCount(Connection con) {
        String sql = "select count(*) from customers";
        return getValue(con,sql);
    }

    @Override
    public Date getMaxBirth(Connection con) {
        String sql = "select max(birth) from customers";
        return getValue(con,sql);
    }
}
