package bupt.edu.cn.spark.service.impl;

import bupt.edu.cn.spark.common.SpSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.awt.*;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {SparkSqlServiceImpl.class,SpSession.class})
public class SparkSqlServiceImplTest {

    @Autowired
    SparkSqlServiceImpl sparkSqlServiceImpl;
    @Autowired
    SpSession spSession;

//    @Autowired
//    SpSession spSession;

    private String fileUrl = "/Users/kang/Desktop/专利/zl.csv"; // Should be some file on your system
    private String fileUrl2 = "/Users/kang/Desktop/BreadBasket_DMS.csv";
    private String sql = "SELECT * FROM table limit 5";

    private String sql2 = "SELECT Item,SUM(Transaction)" +
            " FROM tableName" +
//            " WHERE column_name operator value" +
            " GROUP BY Item";
    private String tableName = "tableName";

    @Test
    public void sparkSQL() {
        SparkSession spark = spSession.getSparkSession();
        Dataset<Row> result = sparkSqlServiceImpl.sparkSQL(spark,fileUrl2,tableName,sql2);
        System.out.println(result);
        result.show();
        System.out.println("----------------");
        spark.stop();
    }

    @Test
    public void getTableInfo() {
        sparkSqlServiceImpl.getTableInfo(fileUrl2);
    }
}