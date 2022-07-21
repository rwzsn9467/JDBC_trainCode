package transaction;

import Utils.JDBCUtils;
import bean.User;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * Created with IDEA
 *
 *数据库问题的引入,数据库事务分析解决
 * 1.事务：一组逻辑操作单元，使数据从一种状态变换到另一种状态。
 * 2.事务处理原则：保证事务都作为一个工作单元来执行，要么所有的事务都被提交，要么整个事务回滚到最初状态(数据一旦提交就不能回滚)。
 * 3.哪些操作会导致数据的自动提交？
 *      >DDL操作一旦执行都会自动提交
 *          >set autocommit = false 队DDL操作失效
 *      >DML默认情况下一旦执行就会自动提交
 *          >我们可以通过set autocommit = false 的方式取消DML操作的自动提交
 *      >默认在关闭连接时，会自动的提交数据
 *
 * @Auther:InheRit
 * @Date:2022/07/20/11:22
 */
public class TransactionTest {

    /****************************未考虑数据库事务情况的操作***********************/
    @Test
    public void testUpdatetr(){
        String sql1 = "update user_table set balance = balance - 100 where user = ?";
        updateTr1(sql1,"AA");

        //模拟网络异常
        System.out.println(10/0);

        String sql2 = "update user_table set balance = balance + 100 where user = ?";
        updateTr1(sql1,"BB");

        System.out.println("转账成功");
    }
    //通用增删改操作1
    public int updateTr1(String sql,Object ...args){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1.获取连接
            conn = JDBCUtils.getConnection();

            //2.预编译sql语句，返回prepareStatement的实例
            ps = conn.prepareStatement(sql);

            //3.填充占位符
            for (int i = 0 ;i < args.length ; i++){
                ps.setObject(i+1,args[i]);//小心参数错误
            }

            //4.执行
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

            //回复自动提交状态，线程池技术会需要用到这个
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //5.资源关闭
            JDBCUtils.closeResource(conn,ps);
        }

        return 0;
    }



    /****************************考虑数据库事务情况的操作***********************/
    @Test
    public void testUpdateTr2(){
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            System.out.println(conn.getAutoCommit());

            //1.取消数据的自动提交
            conn.setAutoCommit(false);

            String sql1 = "update user_table set balance = balance-100 where user = ?";//有趣的是，balance-100之间不能有空格，要不然结果是诡异的，这里卡了我半个多小时，哭了
            updateTr2(conn,sql1,"AA");

            //模拟网络异常,try出现异常直接跳catch去回滚
            //System.out.println(10/0);

            String sql2 = "update user_table set balance = balance+100 where user = ?";
            updateTr2(conn,sql2,"BB");

            System.out.println("转账成功!");

            //2.提交数据
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            //3.回滚数据
            try {
                conn.rollback();
                System.out.println("转账失败!");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    //通用增删改操作2
    public int updateTr2(Connection conn,String sql,Object ...args){
        PreparedStatement ps = null;
        try {

            //1.预编译sql语句，返回prepareStatement的实例
            ps = conn.prepareStatement(sql);

            //2.填充占位符
            for (int i = 0 ;i < args.length ; i++){
                ps.setObject(i+1,args[i]);//小心参数错误
            }

            //3.执行
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //4.资源关闭
            JDBCUtils.closeResource(null,ps);
        }

        return 0;
    }


    @Test
    public void testTransactionSelect() throws Exception {
        Connection conn = JDBCUtils.getConnection();

        //查看当前连接隔离级别，返回int，到源码查看1对应的隔离级别
        System.out.println(conn.getTransactionIsolation());

        //设置数据库的隔离级别
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        //取消自动提交数据
        conn.setAutoCommit(false);

        String sql = "select user,password,balance from user_table where user=?";
        User user = getInstance(conn, User.class, sql, "CC");

        System.out.println(user);
    }

    @Test
    public void testTransactionUpdate() throws Exception {
        Connection conn = JDBCUtils.getConnection();
        String sql = "update user_table set balance=? where user=?";
        updateTr2(conn,sql,5000,"CC");

        Thread.sleep(15000);

        System.out.println("修改结束");
    }

    public <T> T getInstance(Connection conn,Class<T> clazz,String sql,Object ...args){//泛型
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

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

}
