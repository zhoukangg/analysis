package bupt.edu.cn.spark.service.impl;

import bupt.edu.cn.spark.common.SpSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Serializable;

import java.util.List;

@Service
public class SparkSqlServiceImpl implements Serializable {

    @Autowired
    SpSession spSession;

    public void sparkSQL(String fileUrl,String sql){

        SparkSession spark = spSession.getSparkSession();
        Dataset<String> df = spark.read().textFile(fileUrl).cache();

        // Register the DataFrame as a SQL temporary view
        df.createOrReplaceTempView("table");

        Dataset<Row> sqlDF = spark.sql(sql);
        sqlDF.show();

        spark.stop();
    }

    public void getTableInfo(String fileUrl){
        SparkSession spark = spSession.getSparkSession();
        Dataset<String> df = spark.read().textFile(fileUrl).cache();
        df.printSchema();

        String[] cs = df.columns();
        for (int i =0;i <cs.length;i++){
            System.out.println(df.columns());
        }
        spark.stop();
    }
}
