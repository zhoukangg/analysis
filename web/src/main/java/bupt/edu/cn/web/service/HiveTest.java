package bupt.edu.cn.web.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class HiveTest {
    // 驱动，固定的
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";
    // 默认就是10000端口，ip地址使用hive服务器的
    private static String url = "jdbc:hive2://10.108.211.130:10000/";
    private static String url2 = "jdbc:hive2://10.108.211.130:10000/";
    // hive连接的用户名和密码，默认就算是下面这两个
    private static String user = "hive";
    private static String password = "hive";

    // 公共使用的变量
//    private  Connection conn = null;
//    private  Statement stmt = null;
//    private  ResultSet rs = null;

    // 加载驱动、创建默认连接
    public static Connection init() throws Exception {
        Class.forName(driverName);
        Connection conn = DriverManager.getConnection(url2 + "jishengwei", user, password);
        return conn;
    }

    // 查询所有数据库
    public static void showDatabases() throws Exception {
        //连接
        Connection cn = init();
        Statement st = cn.createStatement();
        //查询
        String sql = "show databases";
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
        //释放
        destory(cn, st, rs);
    }

    // 查询所有数据库表
    public static void showTables() throws Exception {
        //连接
        Connection cn = init();
        Statement st = cn.createStatement();
        //查询
        String sql = "show tables";
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
        //释放
        destory(cn, st, rs);
    }

    public static void selectData() throws Exception {
        //连接
        Connection cn = init();
        Statement st = cn.createStatement();
        //查询
        String sql = "select * from gd_apply_service limit 10";
//        String sql = "select GD_GENERAL_INFO_W.* , GD_PREPREGNANCY_SERVICE.* , GD_BASIC_INFO_DETAIL.* from JSW_DATA.GD_GENERAL_INFO_W left join JSW_DATA.GD_PREPREGNANCY_SERVICE GD_PREPREGNANCY_SERVICE on GD_PREPREGNANCY_SERVICE.ID=GD_GENERAL_INFO_W.ID inner join JSW_DATA.GD_BASIC_INFO_DETAIL GD_BASIC_INFO_DETAIL on GD_BASIC_INFO_DETAIL.ID=GD_GENERAL_INFO_W.ID";
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
        //释放
        destory(cn, st, rs);
    }

    // 释放资源
    public static void destory(Connection conn, Statement stmt, ResultSet rs) throws Exception {
        if (rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
        if (conn != null) {
            conn.close();
        }
    }

    // 测试代码（每次都需要现在加载，执行万后释放）
    public static void main(String[] args) throws Exception {
//        showDatabases();
//    		createTable();
//        showDatabases();
//        showTables();
//    		descTable();
//    		loadData();
        selectData();
//    		countData();
//    		dropTable();
//        destory();

    }
}
