package com.xyc.exer;

import com.xyc.JDBCutils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

/**
 * 从控制台向数据库的表customers中插入一条数据
 * @auther xuyuchao
 * @create 2022-03-18-12:36
 */
public class exer1 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入姓名：");
        String name = scanner.next();
        System.out.print("请输入邮箱：");
        String email = scanner.next();
        System.out.print("请输入生日：");
        String birth = scanner.next();

        String sql = "insert into customers(name,email,birth) values(?,?,?);";
        int result = testCommon(sql,name,email,birth);
        if(result > 0) {
            System.out.println("添加成功!");
        }else{
            System.out.println("添加失败!");
        }
    }

    public static int testCommon(String sql,Object ...args) {
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
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils cr = new JDBCUtils();
            cr.closeResource(con,ps);
        }
        return 0;
    }
}
