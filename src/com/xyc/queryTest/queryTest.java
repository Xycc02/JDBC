package com.xyc.queryTest;

import com.xyc.JDBCutils.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * jdbc查询操作
 * @auther xuyuchao
 * @create 2022-03-04-23:36
 */
public class queryTest {
    @Test
    public void queryTest1() {
        String sql = "select id,name from customers where id = ?";
        com.xyc.queryTest.customer cr = commonQueryTest(sql, 1);
        System.out.println(cr.toString());
    }
    @Test
    public void queryTest2() {
        String sql = "select id,name,birth,email from customers where id = ?";
        com.xyc.queryTest.customer cr = commonQueryTest(sql,10);
        System.out.println(cr.toString());
    }

    //通用查询
    public com.xyc.queryTest.customer commonQueryTest(String sql, Object ...args) {
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
                com.xyc.queryTest.customer cust = new com.xyc.queryTest.customer();
                for(int i = 0;i < columnCount;i++) {
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);
                    //获取列名
//                    String columnName = rsmd.getColumnName(i + 1);
                    String columnLabel = rsmd.getColumnLabel(i+1);
                    //核心部分
                    //给cust对象指定的columnName属性，赋值为columnValue，通过反射实现
                    //1.获取customer类中的名叫columnName的属性
                    Field field = com.xyc.queryTest.customer.class.getDeclaredField(columnLabel);
                    //2.对该属性去私有化
                    field.setAccessible(true);
                    //3.设置该属性的值为columnValue
                    field.set(cust,columnValue);
                }
                return cust;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,ps,rs);
        }
        return null;
    }

    //单独sql查询
    @Test
    public void customersQueryTest() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            con = JDBCUtils.getConnection();
            String sql = "select id,name,email,birth from customers where id = ?";
            ps = con.prepareStatement(sql);
            //填充占位符
            ps.setObject(1,1);
            //执行sql，并返回结果集ResultSet
            resultSet = ps.executeQuery();
            //处理返回结果
            if(resultSet.next()) {
                //获取当前这条数据的各个字段值
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String email = resultSet.getString(3);
                Date birth = resultSet.getDate(4);

                //输出结果
                //方式一
//                System.out.println("id = " + id + ",name = " + name + ",email = " + email + "birth = " + birth);
                //方式二 将数据封装成一个对象
                com.xyc.queryTest.customer cr = new com.xyc.queryTest.customer(id,name,email,birth);
                System.out.println(cr.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,ps,resultSet);
        }
    }
}
