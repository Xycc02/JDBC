package com.xyc.dbutils;

import com.xyc.JDBCutils.JDBCUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.*;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * commons-dbutils是Apache组织提供的一个开源的JDBC工具类库
 * 封装了针对于数据库的增删改查操作
 * @auther xuyuchao
 * @create 2022-03-22-12:15
 */
public class dbutils {
    @Test
    public void testInsert() {
        Connection con = null;
        try {
            QueryRunner runner = new QueryRunner();
            con = JDBCUtils.getDruidConnection();
            String sql = "insert into customers(name,email,birth)values(?,?,?)";
            int update = runner.update(con, sql, "123", "46@qq.com", new Date(123456));
            System.out.println("添加了" + update + "条记录");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils jdbcUtils = new JDBCUtils();
            jdbcUtils.closeResource(con,null);
        }
    }

    /**
     * BeanHandler是ResultSetHandler接口的实现类，用于查询并返回一个对象
     * @throws Exception
     */
    @Test
    public void testQuery1() {
        Connection con = null;
        try {
            QueryRunner runner = new QueryRunner();
            con = JDBCUtils.getDruidConnection();
            String sql = "select id,name,email,birth from customers where id = ?";
            BeanHandler<customer> handler= new BeanHandler<>(customer.class);
            customer cust = runner.query(con, sql, handler, 24);
            System.out.println(cust);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,null);
        }
    }

    /**
     * BeanListHandler是ResultSetHandler接口的实现类，用于封装多条记录构成的集合
     * @throws Exception
     */
    @Test
    public void testQuery2() {
        Connection con = null;
        try {
            QueryRunner runner = new QueryRunner();
            con = JDBCUtils.getDruidConnection();
            String sql = "select id,name,email,birth from customers where id < ?";
            BeanListHandler<customer> handler = new BeanListHandler<>(customer.class);
            List<customer> list = runner.query(con, sql, handler, 24);
            list.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,null);
        }
    }


    /**
     * MapHandler是ResultSetHandler接口的实现类，以map键值对的方式返回一条记录
     * @throws Exception
     */
    @Test
    public void testQuery3() {
        Connection con = null;
        try {
            QueryRunner runner = new QueryRunner();
            con = JDBCUtils.getDruidConnection();
            String sql = "select id,name,email,birth from customers where id = ?";
            MapHandler handler = new MapHandler();
            Map<String, Object> map = runner.query(con, sql, handler, 24);
            System.out.println(map);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,null);
        }
    }

    /**
     * MapListHandler是ResultSetHandler接口的实现类，以map键值对的方式返回多条记录
     * @throws Exception
     */
    @Test
    public void testQuery4() {
        Connection con = null;
        try {
            QueryRunner runner = new QueryRunner();
            con = JDBCUtils.getDruidConnection();
            String sql = "select id,name,email,birth from customers where id < ?";
            MapListHandler handler = new MapListHandler();
            List<Map<String, Object>> listMap = runner.query(con, sql, handler, 24);
            listMap.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,null);
        }
    }

    /**
     * ScalarHandler是ResultSetHandler接口的实现类,用于查询特殊值
     * @throws Exception
     */
    @Test
    public void testQuery5() {
        Connection con = null;
        try {
            QueryRunner runner = new QueryRunner();
            con = JDBCUtils.getDruidConnection();
            String sql = "select count(*) from customers";
            ScalarHandler handler = new ScalarHandler();
            Long count = (Long) runner.query(con, sql, handler);
            System.out.println(count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,null);
        }
    }

    /**
     * 自定义ResultSetHandler接口的实现类
     * @throws Exception
     */
    @Test
    public void testQuery6() {
        Connection con = null;
        try {
            QueryRunner runner = new QueryRunner();
            con = JDBCUtils.getDruidConnection();
            String sql = "select id,name,email,birth from customers where id < ?";
            //匿名方法，实现ResultSetHandler接口中的方法
            ResultSetHandler<customer> handler = new ResultSetHandler<customer>() {
                @Override
                public customer handle(ResultSet resultSet) throws SQLException {
                    if(resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String name = resultSet.getString("name");
                        String email = resultSet.getString("email");
                        Date birth = resultSet.getDate("birth");

                        customer cust = new customer(id, name, email, birth);
                        return cust;
                    }
                    return null;
                }
            };

            customer cust = runner.query(con, sql, handler, 24);
            System.out.println(cust);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,null);
        }
    }
}
