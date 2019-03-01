package bupt.edu.cn.spark.service.impl;

import bupt.edu.cn.spark.common.SpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import scala.Serializable;

@Service
public class TestServiceImpl implements Serializable {

    @Autowired
    SpSession spSession;

    public void example(){
        String logFile = "/Users/kang/Desktop/专利/zl.csv"; // Should be some file on your system
        SparkSession spark = spSession.getSparkSession();
        Dataset<String> logData = spark.read().textFile(logFile).cache();

        long numAs = logData.filter(s -> s.contains("a")).count();
        long numBs = logData.filter(s -> s.contains("b")).count();

        System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);
        spark.stop();
    }

}
