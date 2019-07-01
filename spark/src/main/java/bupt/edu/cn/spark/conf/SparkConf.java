package bupt.edu.cn.spark.conf;

import javax.annotation.Resource;
import java.util.Random;

public class SparkConf {
    public static final String APPNAME = "spark.sql.demo";
    public static final String MASTER = "local[*]";
    public String getAppname(){
        return APPNAME+Math.random();
    }

    private static int getRandom(int count) {
        return (int) Math.round(Math.random() * (count));
    }

    private static String str = "abcdefghijklmnopqrstuvwxyz";

    public static String getRandomString(int length){
        StringBuffer sb = new StringBuffer();
        int len = str.length();
        for (int i = 0; i < length; i++) {
            sb.append(str.charAt(getRandom(len-1)));
        }
        return sb.toString();
    }

}
