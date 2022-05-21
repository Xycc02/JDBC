package com.xyc.Transaction;

import com.xyc.JDBCutils.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * 事务的引入
 * 事务:一组逻辑操作单元，使数据从一种状态变换到另一种状态
 *      >一组逻辑单元：一个或多个DML操作
 * 事务处理的原则:保证所有事务都作为一个工作单元来执行，即使出现了故障，都不能改变这种执行方式。
 * 当在一个事务中执行多个操作时，要么所有的事务都被提交( commit),那么这些修改就永久地保存下来
 * 要么数据库管理系统将放弃所作的所有修改，整个事务回滚( rollback )到最初状态。
 *
 * 数据一旦提交，就不能回滚
 * 哪些操作会导致数据的自动提交？
 *      >DDL操作一旦执行，都会自动提交
 *      >DML操作默认情况下，一旦执行就会自动提交
 *          DML可通过set autocommit = false的方式取消DML操作的自动提交
 *      >默认在关闭连接时，会自动提交数据
 * @auther xuyuchao
 * @create 2022-03-20-14:11
 */
public class TransactionTest {

    @Test
    public void UpdateTest1() throws Exception {
        System.out.println("----------未考虑事务-----------");
        String sql1 = "update user_table set balance = balance - 200 where user = ?";
        testCommon(sql1,"AA");
        //模拟网络异常
        System.out.println(10/0);
        String sql2 = "update user_table set balance = balance + 200 where user = ?";
        testCommon(sql2,"BB");
        System.out.println("转账成功!");
    }
    /**
     * 通用增删改
     * @param sql
     * @param args
     * @return  影响的行数
     * @throws Exception
     */
    public int testCommon(String sql,Object ...args) {
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
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,ps);
        }
        return 0;
    }

    @Test
    public void UpdateTest2() throws Exception {
        System.out.println("-----------------考虑了事务----------------");
        Connection con =null;
        try {
            //先获取一个连接
            con = JDBCUtils.getConnection();

            //此处因为DML语句执行完成后会自动提交，应设置不允许自动提交
            con.setAutoCommit(false);

            String sql1 = "update user_table set balance = balance - 200 where user = ?";
            testCommon(con,sql1,"AA");
            //模拟网络异常
            System.out.println(10/0);
            String sql2 = "update user_table set balance = balance + 200 where user = ?";
            testCommon(con,sql2,"BB");
            //在此处统一提交
            con.commit();
            System.out.println("转账成功!");
        } catch (Exception e) {
            //发生异常回滚数据
            con.rollback();
            e.printStackTrace();
        } finally {
            //在此处统一关闭连接
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,null);
        }
    }
    /**
     * 考虑事务
     * 通用增删改
     * @param sql
     * @param args
     * @return  影响的行数
     * @throws Exception
     */
    public int testCommon(Connection con,String sql,Object ...args) {
        PreparedStatement ps = null;
        try {
            //获取PreparedStatement对象
            ps = con.prepareStatement(sql);
            //填充占位符
            for(int i = 0; i < args.length; i++){
                ps.setObject(i+1,args[i]);
            }
            //执行sql语句
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils cr = new JDBCUtils();
            //此处不应关闭con的连接
            cr.closeResource(null,ps);
        }
        return 0;
    }


    /**
     * 事务的隔离级别在java中的操作
     */


    //查询操作事务
    @Test
    public void testTransactionSelect() throws Exception {
        Connection con = JDBCUtils.getConnection();
        //获取当前连接的隔离级别
        /**
         * int TRANSACTION_NONE             = 0;无隔离级别
         * int TRANSACTION_READ_UNCOMMITTED = 1;读未提交
         * int TRANSACTION_READ_COMMITTED   = 2;读提交
         * int TRANSACTION_REPEATABLE_READ  = 4;可重复读
         * int TRANSACTION_SERIALIZABLE     = 8;序列化 串行
         */
        System.out.println(con.getTransactionIsolation());
        //设置数据库的隔离级别    设置读提交
        con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        //取消数据的自动提交
        con.setAutoCommit(false);
        String sql = "select user,password,balance from user_table where user = ?";
        user cc = getInstance(con, user.class, sql, "CC");
        System.out.println(cc);
    }


    //修改操作事务
    @Test
    public void testTransactionUpdate() throws Exception {
        Connection con = JDBCUtils.getConnection();
        String sql = "update user_table set balance = ? where user = ?";
        testCommon(con,sql,5000,"CC");
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
    public <T> T getInstance(Connection con,Class<T> clazz,String sql,Object ...args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
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
            cr.closeResource(null,ps,rs);
        }
        return null;
    }
}
