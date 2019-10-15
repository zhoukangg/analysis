package bupt.edu.cn.web.service;

import bupt.edu.cn.web.conf.hiveConf;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HiveService {
    private  String driverName = hiveConf.DRIVERNAME;
    private  String url = hiveConf.DATABASEURL;
    private  String user = hiveConf.USER;
    private  String password = hiveConf.PASSWORD;

    // 公共使用的变量
//    private  Connection conn = null;
//    private  Statement stmt = null;
//    private  ResultSet rs = null;

    // 加载驱动、创建默认连接
    public Connection init() throws Exception {
        Class.forName(driverName);
        Connection conn = DriverManager.getConnection(url + hiveConf.DEFAULTNAME, user, password);
        return conn;
    }

    // 加载驱动、创建指定数据库连接
    public Connection init(String database) throws Exception {
        Class.forName(driverName);
        Connection conn = DriverManager.getConnection(url + database, user, password);
        System.out.println("hiveUrl:"+url+database);
        return conn;
    }

    // 加载驱动、创建指定数据库连接
    public Connection init(String hiveUrl, String database) throws Exception {
        Class.forName(driverName);
        Connection conn = DriverManager.getConnection(hiveUrl + database, user, password);
        System.out.println("hiveUrl:" + hiveUrl + database);
        return conn;
    }

    // 释放资源
    public  void destory(Connection conn,Statement stmt) throws Exception {
        if (stmt != null) {
            stmt.close();
        }
        if (conn != null) {
            conn.close();
        }
    }
    // 释放资源
    public  void destory(Connection conn,Statement stmt,ResultSet rs) throws Exception {
        if ( rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
        if (conn != null) {
            conn.close();
        }
    }

//    // 测试代码（每次都需要现在加载，执行万后释放）
//    public  void main(String[] args) throws Exception {
//        init();
////    		createTable();
//        showDatabases();
////        showTables();
////    		descTable();
////    		loadData();
////    		selectData();
////    		countData();
////    		dropTable();
//        destory();
//    }

//    // 创建表
//    public  void createTable(String database) throws Exception {
//        //连接
//        Connection cn = init(database);
//        Statement st = cn.createStatement();
//        //查询
//        String sql = "create table pokes (foo int, bar string)";
//        st.execute(sql);
//        //释放
//        destory(cn, st);
//    }

    // 查询所有数据库
    public List<String> showDatabases() throws Exception {
        //连接
        Connection cn = init();
        Statement st = cn.createStatement();
        //查询
        String sql = "show databases";
        ResultSet rs = st.executeQuery(sql);
        List<String> result= new ArrayList<>();
        while (rs.next()) {
            result.add(rs.getString(1));
            System.out.println(rs.getString(1));
        }
        //释放
        destory(cn, st, rs);
        return result;
    }

    // 查询所有表
    public List<String> showTables(String database) throws Exception {
        //连接
        Connection cn = init(database);
        Statement st = cn.createStatement();
        //查询
        String sql = "show tables";
        ResultSet rs = st.executeQuery(sql);
        List<String> result= new ArrayList<>();
        while (rs.next()) {
            result.add(rs.getString(1));
            System.out.println(rs.getString(1));
        }
        //释放
        destory(cn, st, rs);
        return result;
    }

    public static void main(String[] args) {
        try {
            new HiveService().showDatabases();
        }catch (Exception e){

        }

    }

    // 查看表结构
    public  List<Map<String,String>> descTable(String database,String table) throws Exception {
        //连接
        Connection cn = init(database);
        Statement st = cn.createStatement();
        //查询
        String sql = "desc "+table;
        ResultSet rs = st.executeQuery(sql);
        List<Map<String,String>> result = new ArrayList<>();
        while (rs.next()) {
            System.out.println(rs.getString(1) + "\t" + rs.getString(2));
            Map<String,String> rowData = new HashMap<String,String>();
            rowData.put("name",rs.getString(1));
            rowData.put("type",rs.getString(2));
            result.add(rowData);
        }
        //释放
        destory(cn, st, rs);
        return result;
    }

//    // 加载数据
//    public  void loadData(String filePath) throws Exception {
//        //连接
//        Connection cn = init();
//        Statement st = cn.createStatement();
//        //加载
////        String filePath = "/opt/hive-3.1.0/examples/files/kv1.txt";
//        String sql = "load data local inpath '" + filePath + "' overwrite into table pokes";
//        st.execute(sql);
//        //释放
//        destory(cn, st);
//    }

    // 查询数据
    public  List<Map> selectData(String database,String sql) throws Exception {
        //连接
        Connection cn = init(database);
        Statement st = cn.createStatement();
        //查询
        ResultSet rs = st.executeQuery(sql);
        ResultSetMetaData md = rs.getMetaData(); //获得结果集结构信息,元数据
        int columnCount = md.getColumnCount();   //获得列数
        List<Map> listJson  = new ArrayList<>();//存放数据结果
        while (rs.next()){
            Map<String, String> row = new HashMap<>();
            for (int i = 1; i<columnCount+1;i++){ //从1开始
                String columnvalue = "null";//不考虑为null的话加入map后,map的key值都不存在了，
                if (rs.getString(i) != null){
                    columnvalue = rs.getString(i);
                }
                try{
                    row.put(md.getColumnName(i), columnvalue.toString());
                }catch (Exception e){
                    System.out.println("-------hive数据预览出现一个问题---------");
                }
            }
            listJson.add(row);
        }

        //释放
        destory(cn, st,rs);
        return listJson;
    }

    // 查询数据
    public  List<Map> selectData(String url,String database,String sql) throws Exception {
        //连接
        Connection cn = init(url, database);
        Statement st = cn.createStatement();
        //查询
        ResultSet rs = st.executeQuery(sql);
        ResultSetMetaData md = rs.getMetaData(); //获得结果集结构信息,元数据
        int columnCount = md.getColumnCount();   //获得列数
        List<Map> listJson  = new ArrayList<>();//存放数据结果
        while (rs.next()){
            Map<String, String> row = new HashMap<>();
            for (int i = 1; i<columnCount+1;i++){ //从1开始
                String columnvalue = "null";//不考虑为null的话加入map后,map的key值都不存在了，
                if (rs.getString(i) != null){
                    columnvalue = rs.getString(i);
                }
                System.out.println(columnvalue);
                row.put(md.getColumnName(i),columnvalue);
            }
            listJson.add(row);
        }

        //释放
        destory(cn, st,rs);
        return listJson;
    }


//    // 统计查询（会运行mapreduce作业）
//    public  void countData() throws Exception {
//        String sql = "select count(1) from pokes";
//        rs = stmt.executeQuery(sql);
//        while (rs.next()) {
//            System.out.println(rs.getInt(1) );
//        }
//    }

    // 删除数据库表
    public  void dropTable(String database,String table) throws Exception {
        //连接
        Connection cn = init(database);
        Statement st = cn.createStatement();
        //查询
        String sql = "drop table if exists "+table;
        st.execute(sql);
        //释放
        destory(cn, st);
    }
}
