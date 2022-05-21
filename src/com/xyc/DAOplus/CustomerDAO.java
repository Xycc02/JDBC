package com.xyc.DAOplus;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

/**此接口用于规范针对于customers表的常用操作
 * @auther xuyuchao
 * @create 2022-03-20-16:16
 */
public interface CustomerDAO {
    /**
     * 将cust对象添加到数据库中
     * @param con
     * @param cust
     */
    void insert(Connection con, customer cust);

    /**
     * 针对指定的id删除表中的一条记录
     * @param con
     * @param id
     */
    void deleteById(Connection con,int id);

    /**
     * 根据内存中的cust对象，去修改数据表中的记录
     * @param con
     * @param cust
     */
    void update(Connection con, customer cust);

    /**
     *根据指定的id查询到对应的customer对象
     * @param con
     * @param id
     */
    customer getCustomerById(Connection con, int id);

    /**
     * 查询表中的所有记录构成的集合
     * @param con
     * @return
     */
    List<customer> getAll(Connection con);

    /**
     * 查询表中数据的条数
     * @param con
     * @return
     */
    Long getCount(Connection con);

    /**
     * 查询表中的最大生日
     * @param con
     * @return
     */
    Date getMaxBirth(Connection con);
}
