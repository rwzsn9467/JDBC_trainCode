package Dbutils;

import Utils.JDBCUtils;
import bean.Customer;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.*;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created with IDEA
 * commons-dbutiles是Apache组织提供的一个开源的JDBC工具类库，封装了针对于数据库的增删改查操作
 * @Auther:InheRit
 * @Date:2022/07/21/14:26
 */
public class QueryRunnerTest {

    //测试插入
    @Test
    public void testInsert(){
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection3();

            String sql = "insert into customers(name,email,birth)values(?,?,?)";

            int insertCount = runner.update(conn,sql,"蔡徐坤","caixukun@126.com","1997-09-08");

            System.out.println("添加了"+insertCount+"条记录");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    //测试查询：一条记录
    @Test
    public void testQuery1() throws Exception {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection3();

            String sql = "select id,name,email,birth from customers where id = ?";

            //BeanHandler:是ResultSetHandler接口的实现类，用于封装表中的一条记录
            BeanHandler<Customer> handler = new BeanHandler<>(Customer.class);

            Customer customer = runner.query(conn,sql,handler,10);

            System.out.println(customer);
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    //测试查询：多条记录
    @Test
    public void testQuery2() throws Exception {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection3();

            String sql = "select id,name,email,birth from customers where id < ?";

            //BeanListHandler:是ResultSetHandler接口的实现类，用于封装表中的多条记录构成的集合
            BeanListHandler<Customer> handler = new BeanListHandler<>(Customer.class);

            List<Customer> list = runner.query(conn,sql,handler,20);

            System.out.println(list);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    //测试查询：用map以键值对展现一条记录
    @Test
    public void testQuery3() throws Exception {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection3();

            String sql = "select id,name,email,birth from customers where id < ?";

            MapHandler handler = new MapHandler();

            Map<String, Object> map = runner.query(conn, sql, handler, 10);

            System.out.println(map);//一条记录可这样，多条请遍历

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    //测试查询：用map以键值对展现多条记录
    @Test
    public void testQuery4() throws Exception {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection3();

            String sql = "select id,name,email,birth from customers where id < ?";

            MapListHandler handler = new MapListHandler();

            List<Map<String, Object>> list = runner.query(conn, sql, handler, 10);

            list.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    //测试查询：特殊需求
    @Test
    public void testQuery5() throws Exception {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection3();

            String sql = "select count(*) from customers";

            ScalarHandler handler = new ScalarHandler();

            Long count = (long)runner.query(conn, sql, handler);

            System.out.println(count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    //自定义语句
    @Test
    public void testQuery6() throws Exception {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection3();

            String sql = "select id,name,email,birth from customers where id = ?";
            ResultSetHandler<Customer> handler = new ResultSetHandler<>(){//匿名接口实现类，实现方法

                @Override
                public Customer handle(ResultSet resultSet) throws SQLException {

                    if (resultSet.next()){//获得的是下面输入的数据
                        int id = resultSet.getInt("id");
                        String name = resultSet.getString("name");
                        String email = resultSet.getString("email");
                        Date birth = resultSet.getDate("birth");
                        Customer customer = new Customer(id, name, email, birth);

                        return customer;
                    }
                    return null;
                }
            };

            Customer customer = runner.query(conn,sql,handler,10);

            System.out.println(customer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }
}
