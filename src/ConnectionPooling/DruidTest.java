package ConnectionPooling;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.DataSources;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

/**
 * Created with IDEA
 *
 * @Auther:InheRit
 * @Date:2022/07/21/11:35
 */
public class DruidTest {

    @Test
    public void testGetConnection() throws Exception {
        //方式一类似其他

        //方式二
        Properties pros = new Properties();

        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");

        pros.load(is);

        DataSource source = DruidDataSourceFactory.createDataSource(pros);

        Connection conn = source.getConnection();

        System.out.println(conn);
    }
}
