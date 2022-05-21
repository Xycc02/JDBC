package com.xyc.exer;

import com.xyc.JDBCutils.JDBCUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

/**
 * 代码实现：在 eclipse中建立 java 程序：输入身份证号或准考证号可以查询到学生的基本信息。
 * @auther xuyuchao
 * @create 2022-03-18-12:58
 */
public class exer2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请选择你要输入的类型:");
        System.out.println("a.准考证号");
        System.out.println("b.身份证号");
        String selection = scanner.next();
        if("a".equalsIgnoreCase(selection)) {
            System.out.println("请输入准考证号:");
            String examCard = scanner.next();
            String sql = "select Type,IDCard,ExamCard,StudentName,Location,Grade from examstudent where ExamCard = ?";
            student student = getInstance(student.class, sql, examCard);
            System.out.println(student);
        }else if("b".equalsIgnoreCase(selection)){
            System.out.println("请输入身份证号:");
            String idCard = scanner.next();
            String sql = "select Type,IDCard,ExamCard,StudentName,Location,Grade from examstudent where IDCard = ?";
            student student = getInstance(student.class, sql, idCard);
            System.out.println(student);
        }else{
            System.out.println("您输入的有误，请重新进入程序!");
        }
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
    public static <T> T getInstance(Class<T> clazz,String sql,Object ...args) {
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
            cr.closeResource(con,ps,rs);
        }
        return null;
    }
}
