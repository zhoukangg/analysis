package bupt.edu.cn.spark.common;

import bupt.edu.cn.spark.conf.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Component;

@Component
public class SpSession {

    public SparkSession getSparkSession(){

        return  SparkSession.builder().master(SparkConf.MASTER).appName(SparkConf.APPNAME).getOrCreate();
    }
}
