package com.xyc.DAOplus;

import com.xyc.JDBCutils.JDBCUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO （Data(base) access object）
 * DAO抽象类，不能用来实例化对象
 * @auther xuyuchao
 * @create 2022-03-20-15:52
 */
public abstract class BaseDAO<T> {

    private Class<T> clazz = null;
    //public BaseDAO() {}


    {
        //获取当前BaseDAO子类的父类的泛型
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        //强转
        ParameterizedType paramType = (ParameterizedType) genericSuperclass;
        //获取父类的泛型参数，泛型可能有多个，数组形式存储
        Type[] actualTypeArguments = paramType.getActualTypeArguments();
        //泛型的第一个参数，此处即为customer
        clazz = (Class<T>) actualTypeArguments[0];
    }

    /**
     * 考虑事务
     * 通用增删改
     * @param sql
     * @param args
     * @return  影响的行数
     * @throws Exception
     */
    public int update(Connection con, String sql, Object ...args) {
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
     * orm思想
     * 返回不同表的查询一条记录
     * @param
     * @param sql
     * @param args
     * @param
     * @return
     */
    //泛型参数
    public T getInstance(Connection con,String sql,Object ...args) {
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

    /**
     * orm思想
     * 返回不同类的多条记录，用集合容器储存并返回
     * @param
     * @param sql
     * @param args
     * @param
     * @return
     */
    public List<T> getListInstance(Connection con, String sql, Object ...args) {
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
            cr.closeResource(null,ps,rs);
        }
        return null;
    }

    /**
     * 实现像select count(*) from table where xx = xx; 的sql语句
     * @param con
     * @param sql
     * @param args
     * @param <E>
     * @return
     * @throws SQLException
     */
    public <E> E getValue(Connection con,String sql,Object ...args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            for(int i = 0; i < args.length;i++) {
                ps.setObject(i+1,args[i]);
            }
            rs = ps.executeQuery();
            if(rs.next()) {
                return (E) rs.getObject(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils jdbcUtils = new JDBCUtils();
            jdbcUtils.closeResource(null,ps,rs);
        }
        return null;
    }



}
