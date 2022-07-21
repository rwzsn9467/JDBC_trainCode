package dao;

import Utils.JDBCUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IDEA
 * DAO: data(base) access object
 *封装了针对于数据表操作
 * @Auther:InheRit
 * @Date:2022/07/20/16:39
 */
public abstract class BaseDAO {

    //考虑上事务的增删改
    public int updateTr2DAOImpel(Connection conn, String sql, Object ...args){
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


    //考虑上事务的查询一条记录
    public <T> T getInstanceDAOImpel(Connection conn,Class<T> clazz,String sql,Object ...args){//泛型
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


    //考虑上事务的查询多条记录返回记录构成的集合
    public <T> List<T> getForListDAOImpel(Connection conn,Class<T> clazz, String sql, Object ...args){
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
            JDBCUtils.closeResource(null,ps,rs);
        }

        return null;
    }


    //用于查询特殊值的通用的方法
    public <E> E getValue(Connection conn,String sql,Object ...args){
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);

            for (int i = 0;i < args.length; i++){
                ps.setObject(i+i,args[i]);
            }

            rs = ps.executeQuery();

            if (rs.next()){
                return (E) rs.getObject(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(null,ps,rs);
        }

        return null;
    }
}
