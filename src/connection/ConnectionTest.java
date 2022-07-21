package connection;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static java.sql.DriverManager.getConnection;

public class ConnectionTest {
    //方式一
    /*@Test
    public void testConnection1() throws SQLException {
        Driver driver = new com.mysql.jdbc.Driver();//com.mysql.jdbc.Driver()第三方API

        //提供要连接的数据库
        String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8";

        //将用户名和密码封装在Properties中
        Properties info = new Properties();
        info.setProperty("user","root");
        info.setProperty("password","123456");

        //获取连接
        Connection conn = driver.connect(url,info);

        System.out.println(conn);
    }*/


    //方式二:对方式一的迭代,在下面程序中不出现第三方的api，使程序具有更好的可移值性。
/*    @Test

    public void testConnection2() throws Exception {
        Class clazz = Class.forName("com.mysql.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();//过时了

        //提供要连接的数据库
        String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8";

        //将用户名和密码封装在Properties中
        Properties info = new Properties();
        info.setProperty("user","root");
        info.setProperty("password","123456");

        //获取连接
        Connection conn = driver.connect(url,info);
    }*/


    //方式三：使用DriverMannager替换Driver
/*    @Test
    public void testConnection3() throws Exception {
        //提供三个连接的基本信息
        String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8";
        String user = "root";
        String password = "123456";

        //加载Driver，mysql可不要这一行，jar包自动封装了，别的数据库不行，原则上还是写上这句比较好
        Class.forName("com.mysql.jdbc.Driver");

        //获取连接
        Connection con = DriverManager.getConnection(url,user,password);
        System.out.println(con);
    }*/


    //方式四：将数据库连接需要的4个基本信息声明在配置文件中，通过读取配置文件的方式，获取连接
    @Test
    public void getConnection5() throws IOException, ClassNotFoundException, SQLException {
        //1.读取配置文件中的四个基本信息
        InputStream in = ConnectionTest.class.getClassLoader().getResourceAsStream("jdbc.properties");

        Properties pros = new Properties();
        pros.load(in);

        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driverClass = pros.getProperty("driverClass");

        //2.加载驱动
        Class.forName(driverClass);

        //3.获取连接
        Connection conn = getConnection(url, user, password);
        System.out.println(conn);
    }
}
