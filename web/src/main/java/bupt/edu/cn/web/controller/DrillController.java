package bupt.edu.cn.web.controller;

import bupt.edu.cn.web.common.WebConstant;
import bupt.edu.cn.web.pojo.Diagram;
import bupt.edu.cn.web.pojo.DrillDim;
import bupt.edu.cn.web.service.DiagramService;
import bupt.edu.cn.web.service.NewOptionService;
import bupt.edu.cn.web.service.QueryService;
import bupt.edu.cn.web.util.SQLGenerate;
import bupt.edu.cn.web.util.StringUtil;
import bupt.edu.cn.web.util.chartsBase;
import com.alibaba.fastjson.JSON;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
public class DrillController {

    @Autowired
    DiagramService diagramService;

    @Autowired
    NewOptionService newoptionService;

    @Autowired
    QueryService queryService;

    @Autowired
    bupt.edu.cn.web.service.DrillService drillService;

    @Autowired
    bupt.edu.cn.web.service.DataTableInfoService dataTableInfoService;

    @Autowired
    bupt.edu.cn.spark.service.impl.SparkSqlServiceImpl sparkSqlService;

    @RequestMapping("/drillDimSet")
    public String drilloptionset(String tablename, String drilldims, String fileUrl){
        System.out.println("开始设置一个上卷下钻的维度值");
        JSONObject result = new JSONObject();
        DrillDim drillDim = drillService.createDrillDim(tablename,drilldims);
        Map newmap = dataTableInfoService.getCsvDim(fileUrl);
        Object[] objs = (Object[]) newmap.get("meas");
        String[] fileMeas = new String[objs.length];
        for (int i = 0;i <objs.length;i++)
            fileMeas[i] = objs[i].toString();
        //        获取该表的所有维度，并对其进行建模计算
        String[] drilldimArray = drilldims.split(",");

        String sql = "";
        SQLGenerate sqlGenerate = new SQLGenerate();
        sql = sqlGenerate.buildWithDrillDims(tablename,fileMeas,drilldimArray);

        System.out.println("开始生成模型");
        sparkSqlService.DrillFileOutput(fileUrl, tablename, sql, drillDim.getId());
        System.out.println("模型创建完成");

        result.put("tablename",tablename);
        result.put("drilldims",drilldimArray);
        result.put("meas",fileMeas);
        result.put("drillID",drillDim.getId());

        return result.toString();
    }

    @RequestMapping("/DataScrollDrill")
    public String DataScrollDrill(String userId, String dataSourceId, String dim, String mea, int year, int month, int day, int season, int chartType,
                                  String tableName, HttpServletResponse response, HttpServletRequest request){

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

        String fileName = tableName + "-" + measArr[1] + "_" + measArr[0];

        SQLGenerate sqlGenerate = new SQLGenerate();
        String sql = "";
//        String pathurl = "/home/fatbird/workspace/";
        String pathurl = "/Users/user1/Desktop/";
        sql = sqlGenerate.getWithScrollDrill(fileName, measArr, year, season, month, day);
        System.out.println("The SQL is : " + sql);
        List<Map> listJson = queryService.getQueryDataWithDrillParams(pathurl+fileName,fileName,sql);

        //整理一下最后的list
        final String colName = StringUtil.getcolname(listJson);
        String colNameInCN = "";
        switch (colName){
            case "year":
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
        diagram = diagramService.createDiagram("-1","unset",jo.toString(),"2",userId,dataSourceId);
        String str_newDiagram = new chartsBase().transDiagram(2,chartType,diagram.getChart());
        diagramService.updateDiagram(diagram.getId() + "", diagram.getName(), str_newDiagram, "5", userId + "");

        re.put("option",new JSONObject(str_newDiagram));
        re = newoptionService.diagramINresult(re,diagram);
        re.put("year",year);
        re.put("month",month);
        re.put("day",day);
        re.put("dataSourceId",diagram.getDataSourceId());
        re.put("drillflag",true);

        result.put("datum",re);
        result.put("result", WebConstant.QUERY_SUCCESS.isResult());
        result.put("reason",WebConstant.QUERY_SUCCESS.getReason());
        return result.toString();
    }

    @RequestMapping("drillData")
    public String drillData(String userId, String dataSourceId, Long drillID, String paramsValue, String mea, String dataType,
                            HttpServletResponse response, HttpServletRequest request){
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("----------------drillData-----------");
        JSONObject result = new JSONObject();
        DrillDim drillDim = drillService.getDrillDimByID(drillID);
        String tableName = drillDim.getTablename() + "-" + drillID;     //获取真实的表名
        String[] dimsArr = drillDim.getDims().split(",");
        String[] paramsArr = paramsValue.split(",");
        String[] measArr = mea.split("\\.");
        SQLGenerate sqlGenerate = new SQLGenerate();

        String drillSQL = "";
        drillSQL = sqlGenerate.buildWithDrillParams(tableName,dimsArr,paramsArr,measArr);
        String pathurl = "/Users/user1/Desktop/";
        List<Map> listJson = queryService.getQueryDataWithDrillParams(pathurl+tableName,tableName,drillSQL);
        //为了普适性的option的生成，包括时间和其他，地图类的option单独生成
        JSONObject re = new JSONObject();
        Diagram diagram = new Diagram();
        JSONObject jo = new JSONObject();

        if (dataType.equals("map")){
            System.out.println("开始对地图数据进行分析");
            String deepestParamValue = "china";
            if (paramsArr.length > 0){
                deepestParamValue = paramsArr[paramsArr.length - 1];
            }
            jo = newoptionService.createOptionOnMap(listJson,measArr,deepestParamValue);
        }else{
            System.out.println("普适性的数据上卷下钻分析");
            List<String> mea_fun = new ArrayList<>();
            mea_fun.add(measArr[0] + "_" + measArr[1]);
            String deepestParamName = "";
            String[] drillDimArr = new String[1];
            if (paramsArr.length > 0){      //传入的paramValue参数不为空
                if (paramsArr.length == dimsArr.length)     //适用于所有维度都有对应的值的情况(没什么意义)
                    deepestParamName = dimsArr[paramsArr.length-1];
                else
                    deepestParamName = dimsArr[paramsArr.length];
            }else {                         //传入的paramValue参数为空
                deepestParamName = dimsArr[0];
            }
            System.out.println("深度的列名为：" + deepestParamName);
            drillDimArr[0] = deepestParamName;
            jo = newoptionService.newcreateOptionSpark(drillDimArr,mea_fun,listJson);
            System.out.println(jo);
        }
        re.put("option",jo);
        diagram = diagramService.createDiagram("-1","unset",jo.toString(),"2",userId,dataSourceId);
        re = newoptionService.diagramINresult(re,diagram);
        re.put("drillflag",true);
        result.put("datum",re);
        result.put("result", WebConstant.QUERY_SUCCESS.isResult());
        result.put("reason",WebConstant.QUERY_SUCCESS.getReason());

        return result.toString();
    }
}
