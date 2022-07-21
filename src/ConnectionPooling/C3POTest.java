package ConnectionPooling;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IDEA
 * c3p0连接池
 * @Auther:InheRit
 * @Date:2022/07/20/21:19
 */
public class C3POTest {
    //方式一
    @Test
    public void testGetConnection1() throws PropertyVetoException, SQLException {
        //获取c3p0连接池
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass( "com.mysql.jdbc.Driver" );
        cpds.setJdbcUrl( "jdbc:mysql://localhost:3306/test" );
        cpds.setUser("root");
        cpds.setPassword("123456");

        //通过设置相关的参数对数据库连接池进行管理
        //设置初始时数据库连接池中的连接数
        cpds.setInitialPoolSize(10);

        Connection conn = cpds.getConnection();
        System.out.println(conn);

        //销毁连接池(一般不销毁)
        DataSources.destroy(cpds);
    }


    //方式二:配置文件
    @Test
    public void testGetConnection2() throws Exception {
        ComboPooledDataSource cpds = new ComboPooledDataSource("helloc3p0");
        Connection conn = cpds.getConnection();

        System.out.println(conn);
    }
}
