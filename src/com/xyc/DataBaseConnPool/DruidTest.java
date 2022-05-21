package com.xyc.DataBaseConnPool;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

/**
 * @auther xuyuchao
 * @create 2022-03-22-11:53
 */
public class DruidTest {
    @Test
    public void getConnectionTest() throws Exception {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
        Properties pros = new Properties();
        pros.load(is);
        DataSource source = DruidDataSourceFactory.createDataSource(pros);
        Connection con = source.getConnection();
        System.out.println(con);
    }
}
