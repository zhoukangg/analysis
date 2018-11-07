package bupt.edu.cn.spark.service.impl;

import bupt.edu.cn.spark.common.SpSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {SparkSqlServiceImpl.class,SpSession.class})
public class SparkSqlServiceImplTest {

    @Autowired
    SparkSqlServiceImpl sparkSqlServiceImpl;

//    @Autowired
//    SpSession spSession;

    private String fileUrl = "/Users/kang/Desktop/专利/zl.csv"; // Should be some file on your system
    private String url = "SELECT * FROM table limit 5";
    private String url2 = "SELECT column_name, aggregate_function(column_name)" +
            "FROM table_name" +
            "WHERE column_name operator value" +
            "GROUP BY column_name";

    @Test
    public void sparkSQL() {
        sparkSqlServiceImpl.sparkSQL(fileUrl,url);
    }

    @Test
    public void getTableInfo() {
        sparkSqlServiceImpl.getTableInfo(fileUrl);
    }
}