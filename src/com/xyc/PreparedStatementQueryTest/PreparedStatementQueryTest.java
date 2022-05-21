package com.xyc.PreparedStatementQueryTest;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import com.xyc.JDBCutils.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import com.xyc.queryTest.customer;

import com.xyc.orderQueryTest.order;
/**
 * 使用PreparedStatement实现对不同表通用的查询操作
 * @auther xuyuchao
 * @create 2022-03-14-21:44
 */
public class PreparedStatementQueryTest {
    @Test
    public void test2() {
        String sql = "select id,name,email,birth from customers where id < ?";
        List<customer> list = getListInstance(customer.class, sql, 12);
        list.forEach(System.out::println);
    }

    /**
     * 返回不同类的多条记录，用集合容器储存并返回
     * @param clazz
     * @param sql
     * @param args
     * @param <T>
     * @return
     */
    public <T> List<T> getListInstance(Class<T> clazz, String sql,Object ...args) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //获取连接
            con = JDBCUtils.getConnection();
            //返回prepareStatement实例，预编译sql语句
            ps = con.prepareStatement(sql);
            //填充占位符
            for(int i = 0;i < args.length;i++) {
                ps.setObject(i+1,args[i]);
            }
            //返回结果集对象
            rs = ps.executeQuery();

            //获取结果集的元数据:ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过ResultSetMetaData获取结果集的列数
            int columnCount = rsmd.getColumnCount();
            //创建集合容器
            ArrayList<T> list = new ArrayList<>();
            //处理结果集
            while (rs.next()) {
                //创建对象(不知道要创建哪个类，运用反射，得到clazz的实例)
                T t = clazz.newInstance();
                //设置每个记录的值给t对象的属性赋值
                for(int i = 0;i < columnCount;i++) {
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);
                    //获取列名
//                    String columnName = rsmd.getColumnName(i + 1);
                    String columnLabel = rsmd.getColumnLabel(i+1);
                    //核心部分
                    //给t对象指定的columnName属性，赋值为columnValue，通过反射实现
                    //1.获取T类中的名叫columnLabel的属性
                    Field field = t.getClass().getDeclaredField(columnLabel);
                    //2.对该属性去私有化
                    field.setAccessible(true);
                    //3.设置该属性的值为columnValue
                    field.set(t,columnValue);
                }
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,ps,rs);
        }
        return null;
    }


    @Test
    public void test1() {
        String sql = "select user,password,balance from user_table where user = ?";
        userTable ut = getInstance(userTable.class, sql, "AA");
        System.out.println(ut);
        System.out.println("-------------------------------");
        String sql1 = "select order_id orderId,order_name orderName,order_date orderDate from `order` where order_id = ?";
        order o = getInstance(order.class, sql1, 2);
        System.out.println(o);
    }

    /**
     * 返回不同表的查询一条记录
     * @param clazz
     * @param sql
     * @param args
     * @param <T>
     * @return
     */
    //泛型方法、泛型参数
    public <T> T getInstance(Class<T> clazz,String sql,Object ...args) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //获取连接
            con = JDBCUtils.getConnection();
            //返回prepareStatement实例，预编译sql语句
            ps = con.prepareStatement(sql);
            //填充占位符
            for(int i = 0;i < args.length;i++) {
                ps.setObject(i+1,args[i]);
            }
            //返回结果集对象
            rs = ps.executeQuery();

            //获取结果集的元数据:ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过ResultSetMetaData获取结果集的列数
            int columnCount = rsmd.getColumnCount();
            //处理结果集
            if(rs.next()) {
                //创建对象(不知道要创建哪个类，运用反射，得到clazz的实例)
                T t = clazz.newInstance();
                for(int i = 0;i < columnCount;i++) {
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);
                    //获取列名
//                    String columnName = rsmd.getColumnName(i + 1);
                    String columnLabel = rsmd.getColumnLabel(i+1);
                    //核心部分
                    //给t对象指定的columnName属性，赋值为columnValue，通过反射实现
                    //1.获取T类中的名叫columnLabel的属性
                    Field field = t.getClass().getDeclaredField(columnLabel);
                    //2.对该属性去私有化
                    field.setAccessible(true);
                    //3.设置该属性的值为columnValue
                    field.set(t,columnValue);
                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,ps,rs);
        }
        return null;
    }
}



