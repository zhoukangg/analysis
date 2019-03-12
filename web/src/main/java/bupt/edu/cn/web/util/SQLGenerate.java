package bupt.edu.cn.web.util;

import breeze.linalg.dim;
import bupt.edu.cn.kylin.service.KylinQueryService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SQLGenerate {

    @Autowired
    KylinQueryService kqs;

    /**
     * 生成宽表SQL
     * @param str
     * @return
     */
    public String[] getFaltTableSql(String str){
        String SQL = "";
        String sql1 = "";
        String sql2 = "";
        String tables = "";
        JSONObject jsonObject = new JSONObject(str);
        String factTable = jsonObject.getString("fact_table");
        System.out.println(factTable);
        String[] factTableSplit = factTable.split("\\.");
        System.out.println(factTableSplit.length);
        String factTableName = factTableSplit[1];
        System.out.println(factTableName);
        tables = factTable;
        sql1 = "select " + factTableName + ".* ";
        sql2 = "from " + factTable +" ";

        JSONArray lookups = jsonObject.getJSONArray("lookups");

        for (int i = 0; i<lookups.length();i++){
            JSONObject lookup = lookups.getJSONObject(i);
            String table = lookup.getString("table");
            String alias = lookup.getString("alias");

//            String[] tableSplit = table.split("\\.");
//            String tableName = tableSplit[1];

            sql1 = sql1 + ", "+ alias +".* ";
            JSONObject jo = lookup.getJSONObject("join");
            String type = jo.getString("type");
//            System.out.println(jo.get("primary_key").toString().getClass());
            String[] primary_key = (jo.get("primary_key").toString().substring(1,jo.get("primary_key").toString().length()-1)).split(",");
            String[] foreign_key = (jo.get("foreign_key").toString().substring(1,jo.get("foreign_key").toString().length()-1)).split(",");
            tables = tables + "," + table ;
            sql2 = sql2 + type + " join " + table+" "+alias + " on ";
            sql2 = sql2 + primary_key[0].substring(1,primary_key[0].length() - 1)+"="+foreign_key[0].substring(1,foreign_key[0].length() - 1)+" ";
            if (primary_key.length>1){
                for (int j = 1;j<primary_key.length;j++){
                    sql2 = sql2 + "and " + primary_key[j].substring(1,primary_key[j].length() - 1)+"="+foreign_key[j].substring(1,foreign_key[j].length() - 1) + " ";
                }
            }
        }
        SQL = sql1 + sql2;
        String[] result = new String[2];
        result[0] = SQL;
        result[1] = tables;
        return result;
    }

    public String getbydim(String[] dim, String[] mea, String tablename){
        String hdl = " from " + tablename;
        String dimString = "";
        String meaString = "";
        if (dim.length==0 || mea.length==0){
            return "ERROR";
        }

        if (mea.length == 1){
            meaString += mea[0];
        }else{
            for (int i = 0;i < mea.length-1;i++){
                meaString += (mea[i] + ",");
            }
            meaString += mea[mea.length-1];
        }

        if (dim.length == 1){
            dimString = dimString + dim[0];
        }else{
            for (int i = 0;i < dim.length-1;i++){
                dimString += (dim[i] + ",");
            }
            dimString += dim[dim.length-1];
        }
        hdl = "select " + dimString + ","+ meaString + hdl;
        return hdl;
    }

    public String getWithGroup(String[] dim, List<String> fun, List<String> mea, String tablename,String fileType,String fileUrl,String routeStr,String limit){

        String hdl ="";
        if (fileType.equals("CSV")){ //生成sparkSQL
            hdl = " from `" + tablename + "` group by ";
            String dimString = "";
            String meaString = "";

            if (dim.length==0){
                return "ERROR";
            }

            if (dim.length == 1){
                dimString = dimString +"`"+ dim[0]+"`";
            }else{
                for (int i = 0;i < dim.length-1;i++){
                    dimString += ("`"+dim[i]+"`" + ",");
                }
                dimString =dimString +"`"+ dim[dim.length-1]+"`";
            }


            if (mea.size() != 0) {
                if (mea.size() == 1) {
                    meaString += (fun.get(0)+"(`" + mea.get(0) + "`) as `" + mea.get(0)+"_"+fun.get(0)+"`");
                } else {
                    for (int i = 0; i < mea.size() - 1; i++) {
                        meaString += (fun.get(i)+"(`" + mea.get(i) + "`) as `" + mea.get(i)+"_"+fun.get(i) + "`,");
                    }
                    meaString += (fun.get(mea.size() - 1)+"(`" + mea.get(mea.size() - 1) + "`) as `" + mea.get(mea.size() - 1)+"_"+fun.get(mea.size()-1)+"`");
                }

                hdl = "select " + meaString + ","+ dimString + hdl + dimString + " limit "+limit;
            }else {
                hdl = "select " + dimString  + hdl + dimString + " limit "+limit;
            }
        }else if (fileType.equals("FALT")){
            if (routeStr.startsWith("kylin")){ //生成kylin SQL
                hdl = " from " + fileUrl.split(" from ")[1] + " group by ";
                String dimString = "";
                String meaString = "";

                if (dim.length==0){
                    return "ERROR";
                }

                if (dim.length == 1){
                    dimString = dimString + dim[0];
                }else{
                    for (int i = 0;i < dim.length-1;i++){
                        dimString += (dim[i] + ",");
                    }
                    dimString =dimString + dim[dim.length-1];
                }


                if (mea.size() != 0) {
                    if (mea.size() == 1) {
                        meaString += (fun.get(0)+"(" + mea.get(0) + ") ");
                    } else {
                        for (int i = 0; i < mea.size() - 1; i++) {
                            meaString += (fun.get(i)+"(" + mea.get(i) + ") " + ",");
                        }
                        meaString += (fun.get(mea.size() - 1)+"(" + mea.get(mea.size() - 1) + ") ");
                    }

                    hdl = "select " + meaString + ","+ dimString + hdl + dimString + " limit "+limit;
                }else {
                    hdl = "select " + dimString  + hdl + dimString + " limit "+limit;
                }
            }else {  //生成宽表 hive SQL
                hdl = " from " + fileUrl.split(" from ")[1] + " group by ";
                String dimString = "";
                String meaString = "";

                if (dim.length==0){
                    return "ERROR";
                }

                if (dim.length == 1){
                    dimString = dimString + dim[0];
                }else{
                    for (int i = 0;i < dim.length-1;i++){
                        dimString += (dim[i] + ",");
                    }
                    dimString =dimString + dim[dim.length-1];
                }

                if (mea.size() != 0) {
                    if (mea.size() == 1) {
                        meaString += (fun.get(0)+"(" + mea.get(0) + ") as `" + mea.get(0)+"_"+fun.get(0)+"`");
                    } else {
                        for (int i = 0; i < mea.size() - 1; i++) {
                            meaString += (fun.get(i)+"(" + mea.get(i) + ") as `" + mea.get(i)+"_"+fun.get(i) + "`,");
                        }
                        meaString += (fun.get(mea.size() - 1)+"(" + mea.get(mea.size() - 1) + ") as `" + mea.get(mea.size() - 1)+"_"+fun.get(mea.size()-1)+"`");
                    }

                    hdl = "select " + meaString + ","+ dimString + hdl + dimString + " limit "+limit;
                }else {
                    hdl = "select " + dimString  + hdl + dimString + " limit "+limit;
                }
            }


        } else { //生成hive SQL
            hdl = " from " + tablename + " group by ";
            String dimString = "";
            String meaString = "";

            if (dim.length==0){
                return "ERROR";
            }

            if (dim.length == 1){
                dimString = dimString + dim[0];
            }else{
                for (int i = 0;i < dim.length-1;i++){
                    dimString += (dim[i] + ",");
                }
                dimString =dimString + dim[dim.length-1];
            }


            if (mea.size() != 0) {
                if (mea.size() == 1) {
                    meaString += (fun.get(0)+"(" + mea.get(0) + ") as " + mea.get(0)+"_"+fun.get(0));
                } else {
                    for (int i = 0; i < mea.size() - 1; i++) {
                        meaString += (fun.get(i)+"(" + mea.get(i) + ") as " + mea.get(i)+"_"+fun.get(i) + ",");
                    }
                    meaString += (fun.get(mea.size() - 1)+"(" + mea.get(mea.size() - 1) + ") as " + mea.get(mea.size() - 1)+"_"+fun.get(mea.size()-1));
                }

                hdl = "select " + meaString + ","+ dimString + hdl + dimString + " limit "+limit;
            }else {
                hdl = "select " + dimString  + hdl + dimString + " limit "+limit;
            }
        }


        return hdl;
    }

    public String getWithOnemeas(List<String> fun, List<String> mea, String tablename,String fileType,String fileUrl,String routeStr){
        String hdl ="";
        if (fileType.equals("CSV")){
            hdl = " from `" + tablename + "`";
            String meaString = "";

            if (mea.size() == 1)
                meaString += (fun.get(0)+"(`" + mea.get(0) + "`) as `" + mea.get(0)+"_"+fun.get(0) +"`");

            hdl = "select " + meaString  + hdl;
        }else if (fileType.equals("FALT")){
            if (routeStr.startsWith("kylin")){
                hdl = " from " + fileUrl.split(" from ")[1];
                String meaString = "";
                if (mea.size() == 1)
                    meaString += (fun.get(0)+"(" + mea.get(0) + ") ");

                hdl = "select " + meaString  + hdl;
            }else {
                hdl = " from " + fileUrl.split(" from ")[1];
                String meaString = "";
                if (mea.size() == 1)
                    meaString += (fun.get(0)+"(" + mea.get(0) + ") as `" + mea.get(0)+"_"+fun.get(0) +"`");

                hdl = "select " + meaString  + hdl;
            }
        }else{
            hdl = " from " + tablename;
            String meaString = "";

            if (mea.size() == 1)
                meaString += (fun.get(0)+"(" + mea.get(0) + ") as " + mea.get(0)+"_"+fun.get(0));

            hdl = "select " + meaString  + hdl;
        }


        return hdl;
    }

    public String getWithScrollDrill(String tablename, String[] measArr, int year, int season, int month, int day){
        //上卷下钻专用SQL生成器
        String result = "";
//Y        sql = "select month, min(`Transaction_min`) as Transaction_min from `BreadBasket_DMS-Transaction_min` where year = `2017` group by month";
//M        sql = "select day, min(`Transaction_min`) as Transaction_min from `BreadBasket_DMS-Transaction_min` where year = '2017' and month = '3' group by day";
//XXX      sql = "select year, min(`Transaction_min`) as Transaction_min from `BreadBasket_DMS-Transaction_min` group by year"
//D        sql = "select day, Transaction_min as Transaction_min from `BreadBasket_DMS-Transaction_min` where year = '2017' and month = '3' and day = '11'";
        String paramName = measArr[0] + "(`" + measArr[1] + "_" + measArr[0] + "`)";       //min(`Transaction_min`)
        if (year != -1 && season == -1 && month == -1 && day == -1){        //以1年的4个季度为维度进行计算
            result = "select season, " + paramName + " as `" + measArr[1] + "_" + measArr[0] + "` from `" + tablename + "` where year = '" + year + "' group by season";
        }else if (year != -1 && season != -1 && month == -1 && day == -1) {  //以1季度的3个月为维度进行计算
            result = "select month, " + paramName + " as `" + measArr[1] + "_" + measArr[0] + "` from `" + tablename + "` where year = '" + year + "' and season = '" + season + "' group by month";
        }else if (year != -1 && season != -1 && month != -1 && day == -1){   //以一个月的30天为维度计算
            result = "select day, " + paramName + " as `" + measArr[1] + "_" + measArr[0] + "` from `" + tablename + "` where year = '" + year + "' and season = '" + season + "' and month = '" + month + "' group by day";
        }else if (year != -1 && season != -1 && month != -1 && day != -1){  //以1日为维度进行计算（意义不大）
            result = "select day, `" + measArr[1] + "_" + measArr[0] + "` from `" + tablename + "` where year = '" + year + "' and season = '" + season + "' and month = '" + month + "' and day = '" + day + "'";
        }else if (year == -1 && month == -1 && day == -1 && season == -1){  //以初始的n年为维度进行计算
            result = "select year, " + paramName + " as `" + measArr[1] + "_" + measArr[0] + "` from `" + tablename + "` group by year";
        }else{
            result = "select * from `" + tablename + "` limit 1";
            System.out.println("输入year, month, day数据格式有误.");
        }
        System.out.println("The Drill SQL is :" + result);
        return result;
    }
}
