package com.xyc.orderQueryTest;

import com.xyc.JDBCutils.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * @auther xuyuchao
 * @create 2022-03-09-17:47
 * 列名不一致处理方式
 * 使用sql语句别名，并使别名与order类中属性名保持一致
 * 使用获取别名方法getColumnLabel()代替获取列名方法getColumnName()
 * 尽量都用getColumnLabel()因为如果没有给列取别名的话，则会直接使用该列名
 */
public class orderQueryTest {

    @Test
    public void orderTest1() throws Exception {
        String sql = "select order_id orderId,order_name orderName,order_date orderDate from `order` where order_id = ?";
        order o = orderCommonQuery(sql, 2);
        System.out.println(o.toString());
    }
    /**
     * 关于order表的通用查询
     */
    public order orderCommonQuery(String sql,Object ...args) {
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
                //创建对象
                order o = new order();
                for(int i = 0;i < columnCount;i++) {
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);
                    //获取列的别名
                    String columnLabel = rsmd.getColumnLabel(i+1);

                    //核心部分
                    //给cust对象指定的columnName属性，赋值为columnValue，通过反射实现
                    //1.获取customer类中的名叫columnName的属性
                    Field field = order.class.getDeclaredField(columnLabel);
                    //2.对该属性去私有化
                    field.setAccessible(true);
                    //3.设置该属性的值为columnValue
                    field.set(o,columnValue);
                }
                return o;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,ps,rs);
        }
        return null;
    }

    /**
     * 单独查询order表中的数据
     * @throws Exception
     */
    @Test
    public void orderQueryTest1(){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //获取连接
            con = JDBCUtils.getConnection();
            //返回PreparedStatement对象
            String sql = "select order_id,order_name,order_date from `order` where order_id = ?";
            ps = con.prepareStatement(sql);
            //填充占位符
            ps.setObject(1,1);
            //返回结果集对象
            rs = ps.executeQuery();
            //处理数据
            if(rs.next()) {
                //分别获取当前这条数据的各个字段值
                int orderId = rs.getInt(1);
                String orderName = rs.getString(2);
                Date orderDate = rs.getDate(3);
                //构造对象，将其数据赋值给对象属性
                order o = new order(orderId,orderName,orderDate);
                System.out.println(o.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放资源
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,ps,rs);
        }
    }
}
