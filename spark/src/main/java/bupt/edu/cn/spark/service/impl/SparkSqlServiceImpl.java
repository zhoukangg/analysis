package bupt.edu.cn.spark.service.impl;

import bupt.edu.cn.spark.common.SpSession;
//import bupt.edu.cn.web.pojo.DrillDim;
import org.apache.spark.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Serializable;


import java.util.regex.*;

import static bupt.edu.cn.spark.utils.FileOperate.combineCSV;

@Service
public class SparkSqlServiceImpl implements Serializable {

    @Autowired
    SpSession spSession;

    public Dataset<Row> sparkSQL(SparkSession spark,String fileUrl, String tableName, String sql){
        //NOTICE: QueryService函数使用该函数用来initDiagram
        //NOTICE: SparkSQLController函数使用该函数直接生成的数据
//        String sqll = "select max(`数量`) as `数量_max`,`订购日期` from `中文` group by `订购日期` limit 10";
        try{
            Dataset<Row> df = spark.read().option("header",true).csv(fileUrl);
            df.createOrReplaceTempView("`"+tableName+"`");//不能使用特殊符号如. 等等
            Dataset<Row> sqlDF = spark.sql(sql);
            String pattern = "(19|20)\\d{2}(\\\\|\\/|-|年)((0?\\d)|(1[0-2]))(\\\\|\\/|-|月)((0?\\d)|((1|2)\\d)|3(0|1))日?";
            for (int i=0; i < sqlDF.columns().length;i++){  //注！仅支持一维度
                System.out.println("开始按照上卷下钻进行分析");
                String columnData = sqlDF.first().get(i).toString();
                if (Pattern.matches(pattern,columnData) && sqlDF.columns().length > 1){  //该列匹配卷钻策略
                    String nolimitSql = sql.split(" limit ")[0];
                    String colName = nolimitSql.split(" ")[3].split("`")[1];
                    Dataset<Row> twoColumnData = spark.sql(nolimitSql);  //得到全部数据的一个表
                    twoColumnData = twoColumnData.withColumn(colName,twoColumnData.col(colName).cast("float"));
                    Dataset<Row> dataDS = DatetableTrans.transDS(twoColumnData, i); //解析出年月日和季度
                    sqlDF = twoColumnData.join(dataDS,twoColumnData.col(twoColumnData.columns()[i]).equalTo(dataDS.col("stringTime")),"left_outer").drop("stringTime");//将年月季度日4列加入Dataset中
                    sqlDF = sqlDF.drop(sqlDF.columns()[i]);     //删除掉默认的2017-1-1的列
                    String pathName = "/Users/user1/Desktop/";
//                    String pathName = "/home/fatbird/workspace/";
                    String saveName = pathName + tableName + "-" + colName;
                    System.out.println("Save path is :" + saveName);
                    sqlDF.write().option("header", "true").csv(saveName);
                    combineCSV(tableName + "-" + colName,pathName);
                    switch (colName.split("_")[1]){
                        case "max":
                            sqlDF = sqlDF.groupBy("year").max(colName);
                            sqlDF = sqlDF.withColumn(colName,sqlDF.col("max("+ colName +")").name(colName)).drop("max("+ colName +")");
                            break;
                        case "min":
                            sqlDF = sqlDF.groupBy("year").min(colName);
                            sqlDF = sqlDF.withColumn(colName,sqlDF.col("min("+ colName +")").name(colName)).drop("max("+ colName +")");
                            break;
                        case "sum":
                            sqlDF = sqlDF.groupBy("year").sum(colName);
                            sqlDF = sqlDF.withColumn(colName,sqlDF.col("sum("+ colName +")").name(colName)).drop("sum("+ colName +")");
                            break;
                        case "avg":
                            sqlDF = sqlDF.groupBy("year").avg(colName);
                            sqlDF = sqlDF.withColumn(colName,sqlDF.col("avg("+ colName +")").name(colName)).drop("avg("+ colName +")");
                            break;
                        default:
                            break;
                    }
                }
            }
            return sqlDF;

        }catch (Exception e){
            System.out.println("Exception:SparkSqlServiceImpl.sparkSQL");
            System.out.println(e.toString());
            throw e;
        } finally {
        }
    }

    public void DrillFileOutput(String fileUrl, String tableName, String sql, Long ID){
        SparkSession spark = spSession.getSparkSession();
//        String pathName = "/home/fatbird/workspace/";
        String pathName = "/Users/user1/Desktop/";
        String fileName = tableName + "-" + ID;
        Dataset<Row> df = spark.read().option("header",true).csv(fileUrl);
        df.createOrReplaceTempView("`"+tableName+"`");//不能使用特殊符号如. 等等
        Dataset<Row> sqlDF = spark.sql(sql);
        System.out.println("++++++");
        sqlDF.show(10);
        System.out.println("++++++");
        try {
            sqlDF.write().option("header", "true").csv(pathName + fileName);
            combineCSV( fileName,pathName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void getTableInfo(String fileUrl){
        SparkSession spark = spSession.getSparkSession();
        Dataset<Row> df = spark.read().option("header",true).csv(fileUrl);
        System.out.println(df.first());
        df.printSchema();
        String[] cs = df.columns();
        for (int i =0;i <cs.length;i++){
            System.out.println(df.columns());
        }
        spark.stop();
    }
}
