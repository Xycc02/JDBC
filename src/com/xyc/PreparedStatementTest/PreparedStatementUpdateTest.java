package com.xyc.PreparedStatementTest;

import com.mysql.cj.jdbc.JdbcConnection;
import com.xyc.JDBCutils.JDBCUtils;
import com.xyc.connection.ConnectionTest;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @auther xuyuchao
 * @create 2022-01-25-23:07
 * 使用PreparedStatement代替Statement实现数据表的增删改
 */
public class PreparedStatementUpdateTest {

    //增删改通用
    @Test
    public void test() throws Exception {
        String sql = "update customers set name = ? where id = ?";
        testCommon(sql,"张三",18);
    }
    public void testCommon(String sql,Object ...args) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            //获取连接
            con = JDBCUtils.getConnection();
            //获取PreparedStatement对象
            ps = con.prepareStatement(sql);
            //填充占位符
            for(int i = 0; i < args.length; i++){
                ps.setObject(i+1,args[i]);
            }
            //执行sql语句
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,ps);
        }
    }


    //删除customers表中的一条记录
    @Test
    public void testDelete() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = JDBCUtils.getConnection();
            String sql = "delete from customers where id = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1,20);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,ps);
        }
    }
    //修改customers表中的一条记录
    @Test
    public void testUpdate() {
        //1、获取数据库的连接连接
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = JDBCUtils.getConnection();
            //2、预编译sql语句，返回PreparedStatement对象
            String sql = "update customers set name = ? where id = ?";
            ps = con.prepareStatement(sql);
            //3、填充占位符
            ps.setString(1,"李四");
            ps.setInt(2,19);
            //4、执行sql
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5、资源释放
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,ps);
        }
    }

    //向customers表中添加一条记录
    @Test
    public void testInsert() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            //读取配置文件信息
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");
            Properties pros = new Properties();
            pros.load(is);
            String user = pros.getProperty("user");
            String password = pros.getProperty("password");
            String url = pros.getProperty("url");
            String driver = pros.getProperty("driver");

            //注册驱动
            Class.forName(driver);
            //建立连接
            con = DriverManager.getConnection(url,user,password);

            // 预编译SQL语句，返回PreparedStatement对象
            String sql = "insert into customers(name,email)values(?,?)";// ?是占位符
            ps = con.prepareStatement(sql);

            //填充占位符
            ps.setString(1,"张三");
            ps.setString(2,"zhangsan@qq.com");
//            //格式化日期
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            java.util.Date date = sdf.parse("2000-01-01");
//            ps.setDate(3, (java.sql.Date) new Date(date.getTime()));

            //执行SQL
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            //资源释放
            try {
                if(ps != null)
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if(ps != null)
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
