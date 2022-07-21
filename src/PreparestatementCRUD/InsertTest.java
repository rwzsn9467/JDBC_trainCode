package PreparestatementCRUD;

import Utils.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.jar.JarEntry;

/**
 * Created with IDEA
 *使用PrepareStatement实现批量数据的操作,update、delete本身具有批量操作,在此实现批量插入
 * @Auther:InheRit
 * @Date:2022/07/20/9:10
 *
 *题目：向goods表中插入20000条数据
 * CREATE TABLE goods(
 *  id INT PRIMARY KEY AUTO_INCREMENT,
 *  NAME VARCHAR(25)
 * );
 *
 * 方式一：statement实现（现在不推荐使用）
 * Connection conn = JDBCUtils.getConnection();
 * Statement st = conn.creatStatement();
 * for(int i =1 ; i <= 20000;i++){
 *     String sql = "insert into goods(name)values('name_" + i + "')";
 *     st.execute(sql);
 * }
 *
 * 方式二：PrepareStatement实现
 * PreparedStatement并不能为连续读操作提高速度，但却可以大幅度提高连续写速度。
 *
 * 方式三：方式二基础上
 * 1.addBatch(),executeBatch(),clearBatch()
 * 2.mysql服务器默认是关闭批处理的，我们需要通过一个参数，让mysql开启批处理的支持，?rewriteBatchedStatements=true 写在配置文件的url后面
 *
 * 方式四：方式三基础上取消自动提交，最后再一次性提交全部
 */
public class InsertTest {

    /*方式一：statement实现（现在不推荐使用）*/
    @Test
    public void testInsert1(){
        Connection conn = null;
        Statement st = null;

        try {
            long start = System.currentTimeMillis();

            conn = JDBCUtils.getConnection();
            st = conn.createStatement();
            for(int i =1 ; i <= 20000;i++){//注意是从 1 开始
                String sql = "insert into goods(name)values('name_" + i + "')";
                st.execute(sql);
            }

            long end = System.currentTimeMillis();

            System.out.println("花费的时间为：" + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,st);
        }
    }

    /*方式二：PrepareStatement实现*/
    @Test
    public void testInsert2(){
        Connection conn  = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();

            conn = JDBCUtils.getConnection();

            String sql = "insert into goods(name)values (?)";

            ps = conn.prepareStatement(sql);

            for (int i = 1; i < 20000; i++){
                ps.setObject(1,"name_" + i);

                ps.execute();
            }

            long end = System.currentTimeMillis();

            System.out.println("花费的时间为：" + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,ps);
        }
    }


    /*方式三：方式二的基础上减少交互*/
    @Test
    public void testInsert3(){
        Connection conn  = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();

            conn = JDBCUtils.getConnection();

            String sql = "insert into goods(name)values (?)";

            ps = conn.prepareStatement(sql);

            for (int i = 1; i <= 2000000; i++){
                ps.setObject(1,"name_" + i);

                //1.攒sql
                ps.addBatch();

                if (i % 500 == 0){
                    //2.执行barch
                    ps.executeBatch();

                    //3.清空batch
                    ps.clearBatch();
                }
            }

            long end = System.currentTimeMillis();

            System.out.println("花费的时间为：" + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,ps);
        }
    }

    /*方式四：方式三的基础上取消自动提交*/
    @Test
    public void testInsert4(){
        Connection conn  = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();

            conn = JDBCUtils.getConnection();

            String sql = "insert into goods(name)values (?)";

            ps = conn.prepareStatement(sql);

            //设置不允许自动提交数据
            conn.setAutoCommit(false);

            for (int i = 1; i <= 2000000; i++){
                ps.setObject(1,"name_" + i);

                //1.攒sql
                ps.addBatch();

                if (i % 500 == 0){
                    //2.执行barch
                    ps.executeBatch();

                    //3.清空batch
                    ps.clearBatch();
                }
            }

            //提交上面堆积的数据（一次性提交全部）
            conn.commit();

            long end = System.currentTimeMillis();

            System.out.println("花费的时间为：" + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,ps);
        }
    }
}
