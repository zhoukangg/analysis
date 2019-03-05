package bupt.edu.cn.spark.service.impl;

import org.apache.spark.api.java.function.FlatMapGroupsFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import scala.Tuple3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatetableTrans {
    public static Dataset<Row> transDS(Dataset<Row> data, int i){
        String columnName = data.columns()[i];
        String pattern = "(19|20)\\d{2}(\\\\|\\/|-|年)(0?\\d)(\\\\|\\/|-|月)((0?\\d)|((1|2)\\d)|3(0|1))日?";
        Encoder<Tuple3<String, String, String>> tuple3Encoder = Encoders.tuple(Encoders.STRING(), Encoders.STRING(), Encoders.STRING());
        Dataset<Row> result = data.groupByKey((MapFunction<Row,Tuple3<String,String,String>>) row ->{
            String colName = row.<String>getAs(columnName);
            return new Tuple3<String, String, String>(colName,"","");
        },tuple3Encoder)
                .flatMapGroups((FlatMapGroupsFunction<Tuple3<String, String, String>, Row, Datetable>)(key, values)->{
                    try {
                        List<Datetable> dts = new ArrayList<>();
                        Datetable dt = new Datetable();
                        String time = key._1();
                        int[] formatTime = timeResolve(time);
                        dt.setStringTime(time);
                        dt.setYear(formatTime[0]);
                        dt.setMonth(formatTime[1]);
                        if (formatTime[1] <= 3)
                            dt.setSeason(1);
                        else if (formatTime[1] <= 6)
                            dt.setSeason(2);
                        else if (formatTime[1] <= 9)
                            dt.setSeason(3);
                        else
                            dt.setSeason(4);
                        dt.setDay(formatTime[2]);
                        dts.add(dt);
                        return dts.iterator();      //返回最终格式化数据
                    } catch (Exception e){
                        return new ArrayList<Datetable>().iterator();
                    }
                }, new Datetable().produceBeanEncoder()).toDF();
        return result;
    }

    private static int[] timeResolve (String time){
        int[] result = new int[3];      //0--->年    1--->月     2--->日
        result[0] = 1970;
        result[1] = 1;
        result[2] = 1;
        try{
            String regEx="(\\d+)(\\\\|\\/|-|年)(\\d+)(\\\\|\\/|-|月)(\\d+)日?";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(time);
            if (m.find()){
                result[0] = Integer.parseInt(m.group(1));
                result[1] = Integer.parseInt(m.group(3));
                result[2] = Integer.parseInt(m.group(5));
            }
            return result;
        }catch (Exception e){
            System.out.println("数据格式无法解析: " + time);
            return result;
        }
    }
}
