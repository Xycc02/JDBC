package com.xyc.JDBCutils;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.xyc.connection.ConnectionTest;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @auther xuyuchao
 * @create 2022-02-25-16:06
 */
public class JDBCUtils {


    /**
     * Druid获取数据库连接池的连接
     * @throws Exception
     */
    public static Connection getDruidConnection() throws Exception {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
        Properties pros = new Properties();
        pros.load(is);
        DataSource source = DruidDataSourceFactory.createDataSource(pros);
        Connection con = source.getConnection();
        return con;
    }
    /**
     * 获取数据库的连接
     * @return con
     * @throws Exception
     */
    public static Connection getConnection() throws Exception {
        //1、读取配置文件jdbc.properties中的信息
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");
        Properties pros = new Properties();
        pros.load(is);
        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driver = pros.getProperty("driver");

        //2、加载驱动
        Class.forName(driver);

        //3、获取连接
        Connection con = DriverManager.getConnection(url,user,password);
        return con;
    }

    /**
     * 释放资源
     * @param con
     * @param ps
     */
    public void closeResource(Connection con, PreparedStatement ps) {
        if(con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeResource(Connection con, PreparedStatement ps, ResultSet rs) {
        if(con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
