package PreparestatementCRUD;

import Utils.JDBCUtils;
import org.junit.Test;
import statementCRUD.StatementTest;
import statementCRUD.User;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created with IDEA
 *用PreparedStatement替换Statement，解决SQL注入问题
 * 除了解决Statement的拼串、sql问题之外，PreparedStatementh还有哪些好处呢?
 * 1.PreparedStatement操作Blob的数据，而Statement做不到
 * 2.PreparedStatement可以实现更高效的批量操作
 * @Auther:InheRit
 * @Date:2022/07/19/16:31
 */
public class PreparedStatementTest {

    @Test
    public void testLogin() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入用户名：");
        String user = scanner.next();
        System.out.println("请输入密码：");
        String password = scanner.next();

        String sql = "SELECT user,password FROM user_table WHERE user = ? AND password = ?";//拼串也解决了

        User returnUser = getInstance(User.class,sql,user,password);

        if(returnUser != null){
            System.out.println("登陆成功");
        }else{
            System.out.println("用户名不存在或密码错误");
        }
    }


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

}
