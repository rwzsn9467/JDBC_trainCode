package Utils;



import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import connection.ConnectionTest;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import static java.sql.DriverManager.getConnection;

/*操作数据库的工具类*/
public class JDBCUtils {


    /**********************暴力法*********************/
    public static Connection getConnection() throws Exception {
        //1.读取配置文件中的四个基本信息
        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");

        Properties pros = new Properties();
        pros.load(in);

        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driverClass = pros.getProperty("driverClass");

        //2.加载驱动
        Class.forName(driverClass);

        //3.获取连接
        Connection conn = DriverManager.getConnection(url, user, password);
        return conn;
    }


    /**********************c3po的数据库连接池技术*********************/
    private static ComboPooledDataSource cpds = new ComboPooledDataSource("helloc3p0");
    public static Connection getConnnection1() throws SQLException{
        Connection conn = cpds.getConnection();

        return conn;
    }


    /**********************DBCP数据库连接池技术*********************/
    private static DataSource source;
    static {//静态代码块，随着类的加载而加载，只加载一次
        try {
            Properties pros = new Properties();

            //方式一：
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp.properties");
/*
        //方式二：
        FileInputStream is = new FileInputStream(new File("src/dbcp.properties"));*/

            pros.load(is);
            //创建一个DBCP数据库连接池
            source = BasicDataSourceFactory.createDataSource(pros);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection2() throws Exception {


        Connection conn = source1.getConnection();

        return conn;
    }


    /**********************Druid数据库连接池技术*********************/
    private static DataSource source1;
    static {
        try {
            //方式二
            Properties pros = new Properties();

            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");

            pros.load(is);

            DataSource source = DruidDataSourceFactory.createDataSource(pros);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection3() throws Exception {
        //方式一类似其他
        Connection conn = source.getConnection();

        return conn;
    }



    //关闭连接
    public static void closeResource(Connection conn, Statement ps){
        try {
            if(ps != null)
                ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if(conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //重载关闭操作
    public static void closeResource(Connection conn, Statement ps, ResultSet rs){
        try {
            if(ps != null)
                ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if(conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if(rs != null)
                rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //使用dbutils.jar中提供的Dbutils工具类，实现资源的关闭
    public static void closeResource1(Connection conn, Statement ps, ResultSet rs){
/*        try {
            DbUtils.close(conn);
            DbUtils.close(ps);
            DbUtils.close(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        DbUtils.closeQuietly(conn);
        DbUtils.closeQuietly(ps);
        DbUtils.closeQuietly(rs);
    }
}

