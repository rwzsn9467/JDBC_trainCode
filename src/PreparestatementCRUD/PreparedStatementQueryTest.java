package PreparestatementCRUD;

import Utils.JDBCUtils;
import bean.Customer;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IDEA
 * 使用PreparedStatemen实现针对不同表的通用的查询操作
 * @Auther:InheRit
 * @Date:2022/07/19/15:55
 */
public class PreparedStatementQueryTest {



    @Test
    public void testGetInstance(){
        String sql = "select id,name,email from customers where id = ?";
        Customer customer = getInstance(Customer.class,sql,12);
        System.out.println(customer);
    }


    //查询的是一条记录
    public <T> T getInstance(Class<T> clazz,String sql,Object ...args){//泛型
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
                T t = clazz.getDeclaredConstructor().newInstance();
                //处理结果集中的每一列
                for (int i = 0 ; i < columnCount ; i++){
                    //获取列值
                    Object columValue = rs.getObject(i+1);

                    //获取每个列的列名String columName = rsmd.getColumnName(i+1);为了针对于表的字段名与类的属性名不相同的情况，用getColumnLabel
                    String columLabel = rsmd.getColumnLabel(i+1);

                    //给t对象指定的columName属性，赋值为columValue：通过反射
                    Field field = clazz.getDeclaredField(columLabel);
                    field.setAccessible(true);
                    field.set(t,columValue);
                }
                return t;
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }

        return null;
    }


    @Test
    public void testGetForList(){
        String sql = "select id,name,email from customers where id < ?";
        List<Customer> list = getForList(Customer.class, sql, 12);
        list.forEach(System.out::println);
    }

    //查询多条记录
    public <T> List<T> getForList(Class<T> clazz,String sql,Object ...args){
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

            //创建集合对象
            ArrayList<T> list = new ArrayList<T>();
            while(rs.next()){
                T t = clazz.getDeclaredConstructor().newInstance();
                //处理结果集中的每一列:给对象指定的属性赋值
                for (int i = 0 ; i < columnCount ; i++){
                    //获取列值
                    Object columValue = rs.getObject(i+1);

                    //获取每个列的列名String columName = rsmd.getColumnName(i+1);为了针对于表的字段名与类的属性名不相同的情况，用getColumnLabel
                    String columLabel = rsmd.getColumnLabel(i+1);

                    //给t对象指定的columName属性，赋值为columValue：通过反射
                    Field field = clazz.getDeclaredField(columLabel);
                    field.setAccessible(true);
                    field.set(t,columValue);
                }
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }

        return null;
    }
}
