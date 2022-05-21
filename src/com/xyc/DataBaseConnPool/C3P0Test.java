package com.xyc.DataBaseConnPool;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @auther xuyuchao
 * @create 2022-03-21-22:26
 */
public class C3P0Test {

    //方式一
    @Test
    public void testGetConnection() throws Exception {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass( "com.mysql.cj.jdbc.Driver" ); //loads the jdbc driver
        cpds.setJdbcUrl( "jdbc:mysql://localhost:3306/test" );
        cpds.setUser("root");
        cpds.setPassword("root");
        //设置初始时数据库连接池的连接数
        cpds.setInitialPoolSize(10);

        Connection con = cpds.getConnection();
        System.out.println(con);
        //销毁连接池（没必要）
        //DataSources.destroy(cpds);
    }

    //方式二，敏感信息放在xml配置文件
    public void testGetCoonection2() throws SQLException {
        ComboPooledDataSource cpds = new ComboPooledDataSource("helloc3p0");
        Connection con = cpds.getConnection();
        System.out.println(con);
    }
}
