package com.xyc.Blob;

import com.xyc.JDBCutils.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * 实现数据批量插入操作
 * 向goods表中插入20000条数据
 * @auther xuyuchao
 * @create 2022-03-18-20:46
 */
public class InsertManyDataTest {
    /**
     * 用Statement实现数据批量插入
     * @throws Exception
     */
    @Test
    public void StatementTest() throws Exception {
        Connection con = JDBCUtils.getConnection();
        Statement statement = con.createStatement();
        for(int i = 1;i <= 2000;i++) {
            String sql = "insert into goods(name) values('good" + i + "')";
            statement.execute(sql);
        }
    }


    /**
     * 用PreparedStatement代替Statement来实现批量插入数据
     * @throws Exception
     */
    @Test
    public void PreparedStatementTest() throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();
            con = JDBCUtils.getConnection();
            String sql = "insert into goods(name) values(?)";
            ps = con.prepareStatement(sql);
            for(int i = 1; i <= 2000;i++) {
                ps.setObject(1,"product" + i);
                ps.execute();
            }
            long end = System.currentTimeMillis();
            System.out.println("花费的时间:" + (end-start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils jdbcUtils = new JDBCUtils();
            jdbcUtils.closeResource(con,ps);
        }
    }

    @Test
    /**
     * 使用addBatch()、executeBatch()、clearBatch()实现数据的批量插入
     * mysql服务器默认是关闭批处理的，需要在配置文件url的后面加上?rewriteBatchedStatements=true
     *
     */
    public void BatchTest() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();
            con = JDBCUtils.getConnection();
            String sql = "insert into goods(name) values(?)";
            ps = con.prepareStatement(sql);
            for(int i = 1; i <= 1000; i++) {
                ps.setObject(1,"good" + i);
                //1、以100条数据为单位攒Batch
                ps.addBatch();
                if(i % 100 == 0) {
                    //2、以100条数据为单位执行Batch
                    ps.executeBatch();
                    //3、每次执行完Batch后清空Batch
                    ps.clearBatch();
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("所花费的时间为:" + (end-start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils jdbcUtils = new JDBCUtils();
            jdbcUtils.closeResource(con,ps);
        }
    }

    /***
     * 获取数据库连接之后，不允许自动提交数据
     */
    @Test
    public void fasterTest() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();
            con = JDBCUtils.getConnection();
            //获取数据库连接之后，不允许自动提交数据
            con.setAutoCommit(false);
            String sql = "insert into goods(name) values(?)";
            ps = con.prepareStatement(sql);
            for(int i = 1; i <= 1000; i++) {
                ps.setObject(1,"good" + i);
                //1、以100条数据为单位攒Batch
                ps.addBatch();
                if(i % 100 == 0) {
                    //2、以100条数据为单位执行Batch
                    ps.executeBatch();
                    //3、每次执行完Batch后清空Batch
                    ps.clearBatch();
                }
            }
            //在此处统一提交数据
            con.commit();
            long end = System.currentTimeMillis();
            System.out.println("所花费的时间为:" + (end-start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils jdbcUtils = new JDBCUtils();
            jdbcUtils.closeResource(con,ps);
        }
    }

}
