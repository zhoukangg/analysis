package bupt.edu.cn.web.conf;

/**
 * @program analysis
 * @description: ${TODO}
 * @author: kang
 * @create: 2019/07/02 12:04
 */
public class hiveConf {
    // 驱动，固定的
    public static String DRIVERNAME = "org.apache.hive.jdbc.HiveDriver";
    // 默认就是10000端口，ip地址使用hive服务器的
    public static String DATABASEURL = "jdbc:hive2://10.108.211.130:10000/";
    // hive 默认的数据库名称
    public static String DEFAULTNAME = "default";
    // 用户、密码
    public static String PASSWORD = "hive";
    public static String USER = "hive";
}
