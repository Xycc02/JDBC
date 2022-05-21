package com.xyc.DAO.Juint;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import com.xyc.DAO.CustomerDAOImpl;
import com.xyc.DAO.customer;
import com.xyc.JDBCutils.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;

import static org.junit.Assert.*;

/**
 * @auther xuyuchao
 * @create 2022-03-21-12:43
 */
public class CustomerDAOImplTest {

    CustomerDAOImpl dao = new CustomerDAOImpl();
    JDBCUtils cr = new JDBCUtils();

    @Test
    public void testInsert() {
        Connection con = null;
        try {
            con = JDBCUtils.getConnection();
            customer cust = new customer(1,"李俊瑶","123456@qq.com",new Date(2000-9-11));
            dao.insert(con,cust);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cr.closeResource(con,null);
        }
    }

    @Test
    public void testDeleteById() {
        Connection con = null;
        try {
            con = JDBCUtils.getConnection();
            dao.deleteById(con,23);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cr.closeResource(con,null);
        }
    }

    @Test
    public void testUpdate() {
        Connection con = null;
        try {
            con = JDBCUtils.getConnection();
            customer cust = new customer(1,"李俊瑶","654321@qq.com",new Date(20000911));
            dao.update(con,cust);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cr.closeResource(con,null);
        }
    }

    @Test
    public void testGetCustomerById() {
    }

    @Test
    public void testGetAll() {
    }

    @Test
    public void testGetCount() {
    }

    @Test
    public void testGetMaxBirth() {
    }
}