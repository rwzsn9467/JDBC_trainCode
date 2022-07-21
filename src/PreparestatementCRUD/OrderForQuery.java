package PreparestatementCRUD;

import Utils.JDBCUtils;
import bean.Customer;
import bean.Order;
import org.junit.Test;
import org.junit.runner.manipulation.Orderer;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * Created with IDEA
 *
 * @Auther:InheRit
 * @Date:2022/07/19/15:03
 */
public class OrderForQuery {

    @Test
    public void testQuery1() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();

            String sql = "select order_id,order_name,order_date from `order` where order_id = ?";

            ps = conn.prepareStatement(sql);

            ps.setObject(1,1);

            rs = ps.executeQuery();
            if(rs.next()){
                int id = (int) rs.getObject(1);
                String name  = (String) rs.getObject(2);
                Date date = (Date) rs.getObject(3);

                Order order = new Order(id,name,date);
                System.out.println(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }
    }

    /*通过起别名的方式解决字段名和列名不一致的问题*/

    @Test
    public void textOrderForQuery(){
        String sql = "select order_id orderId,order_name orderName,order_date orderDate from `order` where order_id = ?";
        Order order = orderForQuery(sql,1);
        System.out.println(order);
    }


    public Order orderForQuery(String sql,Object ...args){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);

            for (int i = 0;i < args.length;i++){
                ps.setObject(i+1,args[i]);
            }

            rs = ps.executeQuery();
            //获取结果集的数据：ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过ResultSetMetaData获取结果集中的列数
            int columnCount = rsmd.getColumnCount();

            if(rs.next()){
                Order order = new Order();
                //处理结果集中的每一列
                for (int i = 0 ; i < columnCount ; i++){
                    //获取列值
                    Object columValue = rs.getObject(i+1);

                    //获取每个列的列名String columName = rsmd.getColumnName(i+1);为了针对于表的字段名与类的属性名不相同的情况，用getColumnLabel
                    String columLabel = rsmd.getColumnLabel(i+1);

                    //给cust对象指定的columName属性，赋值为columValue：通过反射
                    Field field = Order.class.getDeclaredField(columLabel);
                    field.setAccessible(true);
                    field.set(order,columValue);
                }
                return order;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }

        return null;
    }
}
