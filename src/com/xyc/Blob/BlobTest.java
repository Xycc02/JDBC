package com.xyc.Blob;

import com.xyc.JDBCutils.JDBCUtils;
import com.xyc.queryTest.customer;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;

/**
 * @auther xuyuchao
 * @create 2022-03-18-15:07
 */
public class BlobTest {
    /**
     * 向Customers表中插入一条Blob类型的数据
     * @throws Exception
     */
    @Test
    public void testInsert() throws Exception {
        Connection con = JDBCUtils.getConnection();
        String sql = "insert into customers(name,email,birth,photo) values(?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setObject(1,"李四");
        ps.setObject(2,"123456@126.com");
        ps.setObject(3,"2000-09-11");
        File file = new File("C:\\Users\\徐宇超\\Pictures\\Saved Pictures\\1.jpg");
        //文件输入流
        FileInputStream fileInputStream = new FileInputStream(file);
        ps.setBlob(4,fileInputStream);

        ps.execute();
        fileInputStream.close();
        JDBCUtils jdbcUtils = new JDBCUtils();
        jdbcUtils.closeResource(con,ps);

    }

    @Test
    public void testQueryBlob() throws Exception {
        Connection con = JDBCUtils.getConnection();
        String sql = "select id,name,email,birth,photo from customers where id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setObject(1,22);
        ResultSet resultSet = ps.executeQuery();

        if(resultSet.next()) {
            //获取当前数据各个字段的值
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String email = resultSet.getString("email");
            Date birth = resultSet.getDate("birth");


            //将Blob类型的字段下载下来，以文件的方式保存在本地
            Blob photo = resultSet.getBlob("photo");
            InputStream is = photo.getBinaryStream();
            FileOutputStream fos = new FileOutputStream("lisi.jpg");
            byte[] buffer = new byte[1024];
            int len;
            while( (len = is.read(buffer)) != -1) {
                fos.write(buffer,0,len);
            }


            //输出结果(不包含图片)
            customer cust = new customer(id,name,email,birth);
            System.out.println(cust);
        }
        JDBCUtils jdbcUtils = new JDBCUtils();
        jdbcUtils.closeResource(con,ps,resultSet);
    }
}
