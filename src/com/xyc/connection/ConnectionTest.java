package com.xyc.connection;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Properties;

public class ConnectionTest {
    //方式一
    @Test
    public void testConnection01() throws SQLException {
        //获取Driver的实现类对象
        //不建议用这种方法，因为用到了第三方api
        Driver driver = new com.mysql.cj.jdbc.Driver();
        String url = "jdbc:mysql://localhost:3306/test";
        //用户名和密码封装在Properties中
        Properties info = new Properties();
        info.setProperty("user","root");
        info.setProperty("password","root");

        Connection con = driver.connect(url,info);
        System.out.println(con);
    }

    //方式二
    //不出现第三方的api 使程序具有更好的可移植性
    @Test
    public void testConnection02() throws Exception {
        //1.获取Driver实现类对象（利用反射来实现）
        Class clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        //newInstance()实例化的对象只能调用空参构造器
        //在此处空参构造方法为  com.mysql.cj.jdbc.Driver()
        //返回的是Object注意强制转换
        Driver driver = (Driver)clazz.getDeclaredConstructor().newInstance();

        //2.提供要连接的数据库
        String url = "jdbc:mysql://localhost:3306/test";

        //3.提供用户名密码

        //用户名和密码封装在Properties中
        Properties info = new Properties();
        info.setProperty("user","root");
        info.setProperty("password","root");

        //4.获取连接
        Connection con = driver.connect(url,info);
        System.out.println(con);
    }

    //方式三
    //建立连接部分，将DriverManager替换Driver
    @Test
    public void testConnection03() throws SQLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //1、获取Driver实现类对象
        Class clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver)clazz.getDeclaredConstructor().newInstance();

        //2、注册驱动
        DriverManager.registerDriver(driver);

        //3、获取连接
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root","root");
        System.out.println(con);
    }

    //方式四

    @Test
    public void testConnection04() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        //获取Driver实现类对象（注册驱动）
        //实例化Driver类的对象到内存之后，Driver类有一个静态代码段
        //在类加载时，调用DriverManager.registerDriver(new Driver());
        //其实就是自动注册
        Class.forName("com.mysql.cj.jdbc.Driver");
        //建立连接
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root","root");
        System.out.println(con);
    }


    //方式五
    //将敏感信息放入配置文件中进行读取
    @Test
    public void testConnection05() throws IOException, ClassNotFoundException, SQLException {
        //1、读取配置文件jdbc.properties中的信息
        InputStream is = ConnectionTest.class.getClassLoader().getResourceAsStream("jdbc.properties");
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
        System.out.println(con);
    }

    //方式
    public static void main(String[] args) throws SQLException {
        Connection con = null;
        //jdbc驱动
        String driver="com.mysql.cj.jdbc.Driver";
        //这里我的数据库是test
        String url="jdbc:mysql://localhost:3306/test?&useSSL=false&serverTimezone=UTC";
        String user="root";
        String password="root";
        try {
            //注册JDBC驱动程序(利用反射)
            Class.forName(driver);
            //建立连接
            con = DriverManager.getConnection(url, user, password);
            if (!con.isClosed()) {
                System.out.println("数据库连接成功");
            }

            con.close();
        } catch (ClassNotFoundException e) {
            System.out.println("数据库驱动没有安装");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库连接失败");
        }

        //测试执行mysql语句
        //上面con.close()了，因此需要重新连接
        con = DriverManager.getConnection(url, user, password);
        Statement stmt = con.createStatement();
        String sql;
        sql = "select id,name from user";
        ResultSet ret = stmt.executeQuery(sql);
        while (ret.next()){
            String id = ret.getString("id");
            String name = ret.getString("name");

            System.out.print("id: " + id   );
            System.out.println("name: " + name);
        }
        con.close();
    }

}
