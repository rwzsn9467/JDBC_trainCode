package PreparestatementCRUD;


import Utils.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static java.sql.DriverManager.getConnection;

public class Updatetest123 {

    @Test
    public void testComonUpdate(){
        String sql = "delete from customers where id = ?";
        update(sql,3);
    }

    //通用增删改操作
     public void update(String sql,Object ...args){
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
             ps.execute();
         } catch (Exception e) {
             e.printStackTrace();
         }finally {
             //5.资源关闭
             JDBCUtils.closeResource(conn,ps);
         }
     }


/*    *//*添加记录*//*
    @Test
    public void testInsert(){
        //3.获取连接
        Connection conn = null;
        PreparedStatement ps = null;

        try {
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

            conn = getConnection(url, user, password);

            //4.预编译sql语句，返回PrepareStatement的实例
            String sql = "insert into customers(name,email,birth) values (?,?,?)";//?为占位符，有效解决sql语句注入问题
            ps = conn.prepareStatement(sql);

            //5.填充占位符
            ps.setString(1,"哪吒");
            ps.setString(2,"nezha@gmail.com");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = sdf.parse("1000-01-01");
            ps.setDate(3,new Date(date.getTime()));

            //6.执行操作
            ps.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //7.资源的关闭
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

    }*/


    /*修改记录*/
    @Test
    public void testUndate1(){
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            //1.获取连接
            conn = JDBCUtils.getConnection();

            //2.预编译sql语句，返回prepareStatement的实例
            String sql = "update customers set name = ? where id = ?";
            ps = conn.prepareStatement(sql);

            //3.填充占位符
            ps.setObject(1,"莫菲特");
            ps.setObject(2,18);

            //4.执行
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //5.关闭资源
            JDBCUtils.closeResource(conn,ps);
        }


    }
}
