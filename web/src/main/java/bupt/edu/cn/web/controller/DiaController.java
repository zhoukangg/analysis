package bupt.edu.cn.web.controller;
import breeze.linalg.dim;
import bupt.edu.cn.spark.utils.FileOperate;
import bupt.edu.cn.web.common.WebConstant;
import bupt.edu.cn.web.pojo.DataSource;
import bupt.edu.cn.web.pojo.Diagram;
import bupt.edu.cn.web.pojo.DiagramSql;
import bupt.edu.cn.web.repository.DataSourceRepository;
import bupt.edu.cn.web.repository.DiagramRepository;
import bupt.edu.cn.web.repository.DiagramSQLRepository;
import bupt.edu.cn.web.service.DiagramService;
import bupt.edu.cn.web.service.NewOptionService;
import bupt.edu.cn.web.service.QueryService;
import bupt.edu.cn.web.util.QueryRoute;
import bupt.edu.cn.web.util.SQLGenerate;
import bupt.edu.cn.web.util.StringUtil;
import bupt.edu.cn.web.util.chartsBase;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
public class DiaController {

    @Autowired
    DiagramRepository diagramRepository;

    @Autowired
    DiagramSQLRepository diagramSQLRepository;

    @Autowired
    DataSourceRepository dataSourceRepository;

    @Autowired
    DiagramService diagramService;

    @Autowired
    NewOptionService newoptionService;

    @Autowired
    QueryService queryService;

    @Autowired
    QueryRoute queryRoute;

    @RequestMapping("/queryData")
    public String queryData(String userId, String dataSourceId, String dims, String meas, String fileUrl, String tableName, String fileType,String limit){
        if (System.getProperty("os.name").split(" ")[0] == "Windows")
            if (System.getProperty("os.name").split(" ")[0].equals("Windows"))  //为了FatBird电脑的配置 System.setProperty("hadoop.home.dir", "e:/hadoop");
                System.out.println("-----------进入方法 /queryData----------");
        System.out.println("-----------参数1：dims = " + dims);
        System.out.println("-----------参数2：meas = " + meas);
        System.out.println("-----------参数3：fileUrl = " + fileUrl);
        System.out.println("-----------参数4：tableName = " + tableName);
        System.out.println("-----------参数5：fileType = " + fileType);
        String[] dimArr = {};
        String[] funAndMeaArr;
        List<String> meaArr = new ArrayList<>();
        List<String> funArr = new ArrayList<>();

        if (dims.equals("") && meas.equals(""))       //两个都是空的时候直接返回空值
            return "";

        JSONObject result = new JSONObject();

        if(dims != null && !dims.equals("") && !dims.equals(" ")){
            dims = StringUtil.custom_trim(dims,',');
            dimArr = dims.split(",");
            if (fileUrl.startsWith("select ")){
                for (int i = 0;i<dimArr.length;i++) {
                    if (dimArr[i].split("\\.").length == 3){
                        dimArr[i] = dimArr[i].split("\\.")[1]+"."+dimArr[i].split("\\.")[2];
                    }else if (dimArr[i].split("\\.").length == 2){ //GD_GENERAL_INFO_W.EXAMINE_DATE_DAY
                        dimArr[i] = dimArr[i];
                    }
                }
            }
        }
        if(meas != null && !meas.equals("") && !meas.equals(" ")){
            meas = StringUtil.custom_trim(meas,',');
            System.out.println("--------去,后：meas = " + meas);
            funAndMeaArr = meas.split(",");
            for (String item : funAndMeaArr) {
                System.out.println(item);
                item = StringUtil.custom_trim(item,'.'); //去除首尾'.'
                System.out.println("--------去.后：item = " + item);
                String[] itemSplit = item.split("\\.");
                funArr.add(itemSplit[0]);
                if (itemSplit.length == 4){ //操作名.数据库名.表名.维度
                    meaArr.add(itemSplit[2]+"."+itemSplit[3]);
                }else if (itemSplit.length == 2){ //操作名.维度
                    meaArr.add(itemSplit[1]);
                }else if (itemSplit.length == 3){ //操作名.表名.维度
                    meaArr.add(itemSplit[1]+"."+itemSplit[2]);
                }
            }
        }

        //路由
        String routeStr = queryRoute.route(Arrays.asList(dimArr), funArr, meaArr,tableName,fileUrl);
        if (routeStr.equals("hive")){ //hive 查询出来列名都变成小写了
            for (int i = 0;i<dimArr.length;i++){
                dimArr[i] = dimArr[i].toLowerCase();
            }
            for (int i = 0;i<meaArr.size();i++){
                meaArr.set(i,meaArr.get(i).toLowerCase());
            }
        }
        SQLGenerate sqlGenerate = new SQLGenerate();
        //获取SQL
        String sql;
        if (meaArr.size() == 1 && dimArr.length == 0)     //兼容指标卡的特殊Option
            sql =sqlGenerate.getWithOnemeas(funArr,meaArr,tableName,fileType,fileUrl,routeStr);
        else
            sql = sqlGenerate.getWithGroup(dimArr, funArr, meaArr,tableName,fileType,fileUrl,routeStr,limit);
        System.out.println("The SQL is: " + sql);
        List<Map> listJson = queryService.getQueryData(Arrays.asList(dimArr), funArr, meaArr, fileUrl, tableName, sql, routeStr);

//        List<String> mea_fun = new ArrayList<>();
//        for (int i = 0;i<meaArr.size();i++){
//            mea_fun.add(meaArr.get(i)+"_"+funArr.get(i));
//        }
//        JSONObject jo = newoptionService.newcreateOptionSpark(dimArr,mea_fun,listJson);
//
//        String clas ="2";
//        if (mea_fun.size()>1){
//            clas = "4";
//        }
//        Diagram diagram = diagramService.createDiagram("-1","unset",jo.toString(),clas,userId,dataSourceId);
//        JSONObject re = new JSONObject();
//        re.put("option",jo);
//        re.put("diagramId",diagram.getId());
//        re.put("diagramName",diagram.getName());
//        re.put("classificaion",diagram.getClassification());
//        re.put("userId",diagram.getUserId());
//        re.put("dataSourceId",diagram.getDataSourceId());

        result.put("result",WebConstant.QUERY_SUCCESS.isResult());
        result.put("reason",WebConstant.QUERY_SUCCESS.getReason());
        result.put("datum",listJson);

        return result.toString();
    }

    @RequestMapping("/initDiagram")
    public String initDiagram(String userId, String dataSourceId, String dims, String meas, String fileUrl, String tableName, String fileType, String limit,String rows){
        if (System.getProperty("os.name").split(" ")[0] == "Windows")
            if (System.getProperty("os.name").split(" ")[0].equals("Windows"))  //为了FatBird电脑的配置
                System.setProperty("hadoop.home.dir", "e:/hadoop");
        System.out.println("-----------进入方法 /initDiagram----------");
        System.out.println("-----------参数1：dims = " + dims);
        System.out.println("-----------参数2：meas = " + meas);
        System.out.println("-----------参数3：fileUrl = " + fileUrl);
        System.out.println("-----------参数4：tableName = " + tableName);
        System.out.println("-----------参数5：fileType = " + fileType);
        System.out.println("-----------参数6：rows = " + rows);

        if (dims.length() == 0 || dims.charAt(dims.length()-1) == ','){
            dims = dims + rows;
        }else {
            dims = dims +","+ rows;
        }
        String[] dimArr = {};
        String[] funAndMeaArr;
        String[] rowArr = {};
        List<String> meaArr = new ArrayList<>();
        List<String> funArr = new ArrayList<>();

        // 找到真正的 data_source_id
        // !!!!! 如果数据库中没有符合条件的，此处会出错
        DiagramSql diagramSql = new DiagramSql();
        try {
            List<DataSource> dsList = dataSourceRepository.findByFileNameAndFileUrl(tableName, fileUrl);
            dataSourceId = String.valueOf(dsList.get(0).getId());
            diagramSql.setDataSourceId(dataSourceId);
            diagramSql.setUserId(userId);
            diagramSql.setRows(rows);
            diagramSql.setDims(dims);
            diagramSql.setMeas(meas);
            diagramSql.setUpdateTime(new Date());
        } catch (Exception e){
            e.printStackTrace();
        }

        if (dims.equals("") && meas.equals(""))       //两个都是空的时候直接返回空值
            return "";

        JSONObject result = new JSONObject();

        if(dims != null && !dims.equals("") && !dims.equals(" ")){
            dims = StringUtil.custom_trim(dims,',');
            System.out.println("--------去,后：dims = " + dims);
            dimArr = dims.split(",");
            if (fileUrl.startsWith("select ")){
                for (int i = 0;i<dimArr.length;i++) {
                    if (dimArr[i].split("\\.").length == 3){
                        dimArr[i] = dimArr[i].split("\\.")[1]+"."+dimArr[i].split("\\.")[2];
                    }else if (dimArr[i].split("\\.").length == 2){ //GD_GENERAL_INFO_W.EXAMINE_DATE_DAY
                        dimArr[i] = dimArr[i];
                    }
                }
            }
        }
        if(meas != null && !meas.equals("") && !meas.equals(" ")){
            meas = StringUtil.custom_trim(meas,',');
            System.out.println("--------去,后：meas = " + meas);
            funAndMeaArr = meas.split(",");
            for (String item : funAndMeaArr) {
                item = StringUtil.custom_trim(item,'.'); //去除首尾'.'
                String[] itemSplit = item.split("\\.");
                funArr.add(itemSplit[0]);
                if (itemSplit.length == 4){         //操作名.数据库名.表名.维度
                    meaArr.add(itemSplit[2]+"."+itemSplit[3]);
                }else if (itemSplit.length == 2){   //操作名.维度
                    meaArr.add(itemSplit[1]);
                }else if (itemSplit.length == 3){   //操作名.表名.维度
                    meaArr.add(itemSplit[1]+"."+itemSplit[2]);
                }
            }
        }

        if(rows != null && !rows.equals("") && !rows.equals(" ")){
            rowArr = rows.split(",");
        }
        //路由
        String routeStr = queryRoute.route(Arrays.asList(dimArr), funArr, meaArr,tableName,fileUrl);
        if (routeStr.equals("hive")){ //hive 查询出来列名都变成小写了
            for (int i = 0;i<dimArr.length;i++){
                dimArr[i] = dimArr[i].toLowerCase();
            }
            for (int i = 0;i<meaArr.size();i++){
                meaArr.set(i,meaArr.get(i).toLowerCase());
            }
        }
        SQLGenerate sqlGenerate = new SQLGenerate();
        //获取SQL
        String sql;

        String drillFileNameJudge = "-1";
//        String drillpath = "/root/zhoukang/projectFile/";
        String drillpath = "/Users/user1/Desktop/";
        if (meaArr.size() > 0 && funArr.size() > 0 && dimArr.length ==1)
            drillFileNameJudge =tableName + "-" + meaArr.get(0) + "_" + funArr.get(0) + "-" + dimArr[0];
        // 存储计算结果
        List<Map> listJson = new ArrayList<>();
        // 怎么判断是否是上卷下钻
        System.out.println("Judge:::");
        System.out.println(!FileOperate.initDrillJudge(drillpath,drillFileNameJudge));
        System.out.println(rows != null);
        if (!FileOperate.initDrillJudge(drillpath,drillFileNameJudge) ||
                (rows != null && !rows.equals(""))  ||    //多维表格模式
                (meaArr.size()>1 && dimArr.length==1) ||    //雷达图模式
                (meaArr.size() == 1 && dimArr.length==0)    //指标卡模式
        ){     //不是上卷下钻或目录中无之前的文件
//        if (!dims.contains("日期")){     //不是上卷下钻且目录中无之前的文件
//            进入了非上卷下钻的操作
            System.out.println("---------NONONONONONO-------非上卷下钻的操作----------------------");
            if (meaArr.size() == 1 && dimArr.length == 0)     //兼容指标卡的特殊Option
                sql =sqlGenerate.getWithOnemeas(funArr,meaArr,tableName,fileType,fileUrl,routeStr);
            else
                sql = sqlGenerate.getWithGroup(dimArr, funArr, meaArr,tableName,fileType,fileUrl,routeStr,limit);
            diagramSql.setSqlinfo(sql);
            System.out.println("The SQL is: " + sql);
            listJson = queryService.getQueryData(Arrays.asList(dimArr), funArr, meaArr, fileUrl, tableName, sql, routeStr);
        }else if(dimArr.length==1){
//            进入了上卷下钻的操作 &&
            sql = sqlGenerate.getWithScrollDrill(drillFileNameJudge,meas.split("\\."),-1,-1,-1,-1);
            diagramSql.setSqlinfo(sql);
            listJson = queryService.getQueryDataWithDate(drillpath + drillFileNameJudge,drillFileNameJudge,sql);
        }

        Boolean drillflag = false;
        if (listJson.size() != 0)
            if (!listJson.get(0).containsKey(dims) && !(dimArr.length == 0 && meaArr.size()!=0) && !(dimArr.length>1&&meaArr.size()==1) && dimArr.length==1)  //用来判断是否是可以上卷下钻的
                drillflag = true;
        if (drillflag){                             // 为上卷下钻排序并增加一个"年"的后缀
            Collections.sort(listJson, new Comparator<Map>() {  //給整个listJson进行排序
                public int compare(Map o1, Map o2) {
                    Integer date1 = Integer.valueOf(o1.get("year").toString());
                    Integer date2 = Integer.valueOf(o2.get("year").toString());
                    return date1.compareTo(date2);
                }
            });
            for (Map tempmap : listJson){
                tempmap.put("year",tempmap.get("year").toString() + "年");
            }
        }
        //生成图的类型
        String clas ="";
        if (dimArr.length == 0){
            clas = "-2"; //指标卡类型
        }
        if (dimArr.length == 1 && meaArr.size() >1){
            clas = "4"; //雷达图
        }else if (dimArr.length == 1 && meaArr.size() ==1){
            clas = "2"; //面积图
        }else if (dimArr.length == 1 && meaArr.size() == 0){
            clas = "-1"; //只有横轴的半成品图
        }
        if(dimArr.length > 1){
            clas = "-3"; //数据表格类型
        }
        JSONObject re = new JSONObject();
        Diagram diagram = new Diagram();

        if (rowArr.length < 1){ //返回option
            List<String> mea_fun = new ArrayList<>();
            for (int i = 0;i<meaArr.size();i++){
                mea_fun.add(meaArr.get(i)+"_"+funArr.get(i));
            }
            JSONObject jo = newoptionService.newcreateOptionSpark(dimArr,mea_fun,listJson);

            diagram = diagramService.createDiagram("-1","picture",jo.toString(),clas,userId,dataSourceId);
            re.put("option",jo);

        }else if(rowArr.length >0){ //返回数据表格
            diagram = diagramService.createDiagram("-1","picture",listJson.toString(),clas,userId,dataSourceId);

            //整理数据格式
            //构造列结构
            List<String> cowList = new ArrayList<>(Arrays.asList(dimArr));
            List<String> rowList = new ArrayList<>(Arrays.asList(rowArr));
            cowList.removeAll(rowList);
            com.alibaba.fastjson.JSONArray cowJson = new com.alibaba.fastjson.JSONArray();
            for (int i = 0;i<listJson.size();i++){
                com.alibaba.fastjson.JSONArray now = cowJson;
                for (int j = 0;j<cowList.size();j++){
                    String value;
                    if(listJson.get(i).containsKey(cowList.get(j)))
                        value = listJson.get(i).get(cowList.get(j)).toString();
                    else{
                        value=listJson.get(i).get(listJson.get(i).keySet().iterator().next().toString()).toString();
                    }
                    System.out.println(value);
                    int flag = isExistInJSONArray(now,"name",value);
                    if (flag == -1){
                        com.alibaba.fastjson.JSONObject obj = new com.alibaba.fastjson.JSONObject();
                        obj.put("name",value);
                        if (j!= cowList.size()-1){
                            com.alibaba.fastjson.JSONArray last = new com.alibaba.fastjson.JSONArray();
                            obj.put("last",last);
                            now.add(obj);
                            now = last;
                        }else {
                            now.add(obj);
                        }
                    }else {
                        now = now.getJSONObject(flag).getJSONArray("last");
                    }
                }
            }

            //构造行结构和并填充数据
            List<String> mea_fun = new ArrayList<>();
            for (int i = 0;i<meaArr.size();i++){
                mea_fun.add(meaArr.get(i)+"_"+funArr.get(i));
            }
            com.alibaba.fastjson.JSONArray rowJson = new com.alibaba.fastjson.JSONArray();
            for (int i = 0;i<listJson.size();i++){
                com.alibaba.fastjson.JSONArray now = rowJson;
                //构造返回数据的key值
                String cowName;
                if(listJson.get(i).containsKey(cowList.get(0)))
                    cowName = listJson.get(i).get(cowList.get(0)).toString();
                else{
                    cowName=listJson.get(i).get(listJson.get(i).keySet().iterator().next().toString()).toString();
                }

                for (int j = 1;j<cowList.size();j++){
                    cowName = cowName + "_" + listJson.get(i).get(cowList.get(j));
                }

                System.out.println("---------listJson.get(i).toString()------------");
                System.out.println(listJson.get(i).toString());
                for (int j = 0;j<rowArr.length;j++){
                    System.out.println(rowArr[j]);
                    String value = listJson.get(i).get(rowArr[j]).toString();
                    int flag = isExistInJSONArray(now,"name",value);
                    if (flag == -1){
                        com.alibaba.fastjson.JSONObject obj = new com.alibaba.fastjson.JSONObject();
                        obj.put("name",value);
                        if (j!= rowArr.length-1){
                            com.alibaba.fastjson.JSONArray last = new com.alibaba.fastjson.JSONArray();
                            obj.put("last",last);
                            now.add(obj);
                            now = last;
                        }else {
                            com.alibaba.fastjson.JSONObject last = new com.alibaba.fastjson.JSONObject();
                            if (mea_fun.size()<1){
                                last.put(cowName,"");
                            }else {
                                last.put(cowName,listJson.get(i).get(mea_fun.get(0))); //目前只考虑一个度量
                            }
                            obj.put("value",last);
                            now.add(obj);
                        }
                    }else {
                        if (j!= rowArr.length-1){
                            now = now.getJSONObject(flag).getJSONArray("last");
                        } else {
                            com.alibaba.fastjson.JSONObject data = now.getJSONObject(flag).getJSONObject("value");
                            if (mea_fun.size()<1){
                                data.put(cowName,"");
                            }else {
                                data.put(cowName,listJson.get(i).get(mea_fun.get(0))); //目前只考虑一个度量
                            }
                        }
                    }
                }
            }
            com.alibaba.fastjson.JSONObject op = new com.alibaba.fastjson.JSONObject();
            op.put("cows",cowJson);
            op.put("rows",rowJson);
            re.put("option",op);
        }
        diagramSql.setDiagramid(diagram.getId());
        diagramSQLRepository.saveAndFlush(diagramSql);
        re.put("diagramId",diagram.getId());
        re.put("diagramName",diagram.getName());
        re.put("classificaion",diagram.getClassification());
        re.put("userId",diagram.getUserId());
        re.put("dataSourceId",diagram.getDataSourceId());
        re.put("drillflag",drillflag);

        result.put("result",WebConstant.QUERY_SUCCESS.isResult());
        result.put("reason",WebConstant.QUERY_SUCCESS.getReason());
        result.put("datum",re);
        return result.toString();
    }

    public int isExistInJSONArray(com.alibaba.fastjson.JSONArray jsonArray,String name,String value){
        for(int i = 0;i<jsonArray.size();i++){
            if (jsonArray.getJSONObject(i).getString(name).equals(value))
                return i;
        }
        return -1;
    }


    @RequestMapping("/newupdateDiagram")
    public String newupdateDiagram(int diagramId, String diagramName, int diagramType, int userId, HttpServletRequest request, HttpServletResponse response){
        // 解决Ajax跨域请求问题
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("----------------newupdateDiagram-----------");
        System.out.println("diagramId = "+ diagramId +";diagramName = " + diagramId + ";diagramType = " + diagramType + ";userID = "+ userId);
        Optional<Diagram> diagram;
        diagram = diagramRepository.findById(Long.valueOf(diagramId));
        JSONObject result = new JSONObject();
        result.put("result",true);
        result.put("reson","");
        //判断是否存在，新增/更新
        Diagram newDiagram;
        if (diagram.isPresent()){
            newDiagram = diagram.get();
        }else {
            result.put("result",false);
            result.put("reson","No such option");
            return result.toString();
        }
        //获取option
        String ch = newDiagram.getChart();
        //获取转换前option的类型
        System.out.println(ch);
        JSONObject chOption = new JSONObject(ch);
        int int_typeBefore = 0;
        String typeBefore;
        if (chOption.has("value"))
            typeBefore = "indexcard";
        else if (chOption.has("data") && chOption.has("dims") && chOption.has("meas"))
            typeBefore = "excelChart";
        else
            typeBefore = chOption.getJSONArray("series").getJSONObject(0).getString("type");
        switch (typeBefore){
            case "bar":{        //判断是条形还是堆积还是柱状图
                String xType = chOption.getJSONObject("xAxis").getString("type");
                int seriesLength = chOption.getJSONArray("series").length();
                if (chOption.getJSONObject("yAxis").has("data")){
                    int_typeBefore = 6;     //条形图
                } else if(seriesLength > 1) {
                    int_typeBefore = 1;     //堆积图
                } else {
                    int_typeBefore = 0;     //柱状图
                }
                break;
            }
            case "line":{
                //判断是面积图还是折线图
                JSONObject itemstyle = new JSONObject(ch).getJSONArray("series").getJSONObject(0);
                if (itemstyle.has("itemStyle")){
                    int_typeBefore = 2;
                }else{
                    int_typeBefore = 7;
                }
                break;
            }
            case "pie":
                int_typeBefore = 3;
                break;
            case "radar":
                int_typeBefore = 4;
                break;
            case "heatmap":
                int_typeBefore = 10;
                break;
            case "funnel":
                int_typeBefore = 13;
                break;
            case "wordcloud":
                int_typeBefore = 11;
                break;
            case "scatter":
                int_typeBefore = 12;
                break;
            case "gauge":
                int_typeBefore = 8;
                break;
            case "indexcard":
                int_typeBefore = 14;
                break;
            case "excelChart":
                int_typeBefore = 16;
                break;
            default:
        }
        System.out.println("typeBefore: "+int_typeBefore);
        System.out.println("typeAfter: "+diagramType);
        String str_newDiagram = new chartsBase().transDiagram(int_typeBefore,diagramType,ch);

        diagramService.updateDiagram(diagramId + "", diagramName, str_newDiagram, "5", userId + "");

        //组datum对象
        JSONObject datum = new JSONObject();
        datum.put("option",new JSONObject(str_newDiagram));
        datum.put("diagramId",diagramId);
        datum.put("diagramName",diagramName);
//        datum.put("classification",newDiagram.getClassification());
        datum.put("userId",userId);
//        datum.put("dataSourceId",newDiagram.getDataSourceId());
        result.put("datum",datum);

        return result.toString();
    }
    @RequestMapping("/DataScrollDrill")
    public String DataScrollDrill(String userId, String dataSourceId, String dim, String mea, int year, int month, int day, int season, int chartType,
                                  String tableName,HttpServletResponse response,HttpServletRequest request){

        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("----------------DataScrollDrill-----------");
        System.out.println("year = " + year + ", season = " + season + ", month = " + month + ", day = " + day);
        JSONObject result = new JSONObject();

        String[] measArr = mea.split("\\.");
        String[] dimArr = dim.split(",");
        for (int i = 0;i<dimArr.length;i++) {
            if (dimArr[i].split("\\.").length == 3){
                dimArr[i] = dimArr[i].split("\\.")[1]+"."+dimArr[i].split("\\.")[2];
            }else if (dimArr[i].split("\\.").length == 2){
                dimArr[i] = dimArr[i];
            }
        }

        String fileName = tableName + "-" + measArr[1] + "_" + measArr[0] + "-" + dimArr[0];

        SQLGenerate sqlGenerate = new SQLGenerate();
        String sql = "";
//        String pathurl = "/Users/kang/D/projectFile/";
        String pathurl = "/root/zhoukang/projectFile/";
        sql = sqlGenerate.getWithScrollDrill(fileName, measArr, year, season, month, day);
        System.out.println("The SQL is : " + sql);
        List<Map> listJson = queryService.getQueryDataWithDate(pathurl+fileName,fileName,sql);

        //整理一下最后的list
        final String colName = StringUtil.getcolname(listJson);
        String colNameInCN = "";
        switch (colName){
            case "year":
                colNameInCN = "年";
                break;
            case "month":
                colNameInCN = "月";
                break;
            case "day":
                colNameInCN = "日";
                break;
            case "season":
                colNameInCN = "季度";
            default:
                break;
        }
        Collections.sort(listJson, new Comparator<Map>() {  //給整个listJson进行排序
            public int compare(Map o1, Map o2) {
                Integer date1 = Integer.valueOf(o1.get(colName).toString());
                Integer date2 = Integer.valueOf(o2.get(colName).toString());
                return date1.compareTo(date2);
            }
        });
        for (Map tempmap : listJson){
            tempmap.put(colName,tempmap.get(colName).toString() + colNameInCN);
        }
        //整理listJson结束

        JSONObject re = new JSONObject();
        Diagram diagram = new Diagram();
        List<String> mea_fun = new ArrayList<>();
        mea_fun.add(measArr[1] + "_" + measArr[0]);
        JSONObject jo = newoptionService.newcreateOptionSpark(dimArr,mea_fun,listJson);
        diagram = diagramService.createDiagram("-1","picture",jo.toString(),"2",userId,dataSourceId);
        String str_newDiagram = new chartsBase().transDiagram(2,chartType,diagram.getChart());
        diagramService.updateDiagram(diagram.getId() + "", diagram.getName(), str_newDiagram, "5", userId + "");

        re.put("option",new JSONObject(str_newDiagram));
        re.put("diagramId",diagram.getId());
        re.put("diagramName",diagram.getName());
        re.put("classificaion",diagram.getClassification());
        re.put("userId",diagram.getUserId());
        re.put("year",year);
        re.put("month",month);
        re.put("day",day);
        re.put("dataSourceId",diagram.getDataSourceId());
        re.put("drillflag",true);

        result.put("result",WebConstant.QUERY_SUCCESS.isResult());
        result.put("reason",WebConstant.QUERY_SUCCESS.getReason());
        result.put("datum",re);
        return result.toString();
    }
}
