package bupt.edu.cn.web.controller;

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
import bupt.edu.cn.web.util.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static bupt.edu.cn.web.conf.consist.DRILLPATH;

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
    NewOptionService newOptionService;

    @Autowired
    QueryService queryService;

    @Autowired
    QueryRoute queryRoute;

    /**
     * 测试 aop 打印请求参数
     *
     * @param id
     * @param name
     * @return
     */
    @RequestMapping("/aopTest")
    public String aopTest(String id, String name) {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("name", name);
        return result.toString();
    }

    /**
     * 查看数据
     *
     * @param userId
     * @param dataSourceId
     * @param dims
     * @param meas
     * @param fileUrl
     * @param tableName
     * @param fileType
     * @param limit
     * @return
     */
    @RequestMapping("/queryData")
    public String queryData(String userId, String dataSourceId, String dims, String meas, String fileUrl, String tableName, String fileType, String limit) {
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

        if (dims != null && !dims.equals("") && !dims.equals(" ")) {
            dims = StringUtil.custom_trim(dims, ',');
            dimArr = dims.split(",");
            if (fileUrl.startsWith("select ")) {
                for (int i = 0; i < dimArr.length; i++) {
                    if (dimArr[i].split("\\.").length == 3) {
                        dimArr[i] = dimArr[i].split("\\.")[1] + "." + dimArr[i].split("\\.")[2];
                    } else if (dimArr[i].split("\\.").length == 2) { //GD_GENERAL_INFO_W.EXAMINE_DATE_DAY
                        dimArr[i] = dimArr[i];
                    }
                }
            }
        }
        if (meas != null && !meas.equals("") && !meas.equals(" ")) {
            meas = StringUtil.custom_trim(meas, ',');
            System.out.println("--------去,后：meas = " + meas);
            funAndMeaArr = meas.split(",");
            for (String item : funAndMeaArr) {
                System.out.println(item);
                item = StringUtil.custom_trim(item, '.'); //去除首尾'.'
                System.out.println("--------去.后：item = " + item);
                String[] itemSplit = item.split("\\.");
                funArr.add(itemSplit[0]);
                if (itemSplit.length == 4) { //操作名.数据库名.表名.维度
                    meaArr.add(itemSplit[2] + "." + itemSplit[3]);
                } else if (itemSplit.length == 2) { //操作名.维度
                    meaArr.add(itemSplit[1]);
                } else if (itemSplit.length == 3) { //操作名.表名.维度
                    meaArr.add(itemSplit[1] + "." + itemSplit[2]);
                }
            }
        }

        //路由
        String routeStr = queryRoute.route(Arrays.asList(dimArr), funArr, meaArr, tableName, fileUrl);
        if (routeStr.equals("hive")) { //hive 查询出来列名都变成小写了
            for (int i = 0; i < dimArr.length; i++) {
                dimArr[i] = dimArr[i].toLowerCase();
            }
            for (int i = 0; i < meaArr.size(); i++) {
                meaArr.set(i, meaArr.get(i).toLowerCase());
            }
        }

        //获取SQL
        SQLGenerate sqlGenerate = new SQLGenerate();
        String sql;
        if (meaArr.size() == 1 && dimArr.length == 0)     //兼容指标卡的特殊Option
            sql = sqlGenerate.getWithOnemeas(funArr, meaArr, tableName, fileType, fileUrl, routeStr);
        else
            sql = sqlGenerate.getWithGroup(dimArr, funArr, meaArr, tableName, fileType, fileUrl, routeStr, limit);
        System.out.println("The SQL is: " + sql);
        List<Map> listJson = queryService.getQueryData(Arrays.asList(dimArr), funArr, meaArr, fileUrl, tableName, sql, routeStr);

        // 返回结果
        JSONObject result = new JSONObject();
        result.put("result", WebConstant.QUERY_SUCCESS.isResult());
        result.put("reason", WebConstant.QUERY_SUCCESS.getReason());
        result.put("datum", listJson);
        return result.toString();
    }

    /**
     * 创建图表（计算）
     *
     * @param userId
     * @param dataSourceId 数据源ID
     * @param dims         纬度
     * @param meas         度量
     * @param fileUrl      分析文件路径
     * @param tableName
     * @param fileType     文件类型
     * @param limit
     * @param rows
     * @return
     */
    @RequestMapping("/initDiagram")
    public String initDiagram(String userId, String dataSourceId, String dims, String meas, String fileUrl, String tableName, String fileType, String limit, String rows) {
        if (System.getProperty("os.name").split(" ")[0].equals("Windows"))  //为了FatBird电脑的配置
            System.setProperty("hadoop.home.dir", "e:/hadoop");

        if (rows == null)
            rows = "";
        if (limit == null)
            limit = "";

        if (dims.length() == 0 || dims.charAt(dims.length() - 1) == ',') {
            dims = dims + rows;
        } else {
            dims = dims + "," + rows;
        }

        // 纬度和度量都不存在 返回
        if (StringUtil.isEmpty(dims) && StringUtil.isEmpty(meas))
            return "";

        String[] dimArr = {};
        String[] funAndMeaArr;
        String[] rowArr = {};
        List<String> meaArr = new ArrayList<>();
        List<String> funArr = new ArrayList<>();

        // 找到真正的 data_source_id
        // TODO：如果数据库中没有符合条件的，此处会出错
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
        } catch (Exception e) {
            e.printStackTrace();
        }


        // 处理纬度字符串为纬度数组
        if (!StringUtil.isEmpty(dims)) {
            dims = StringUtil.custom_trim(dims, ',');
            System.out.println("--------去,后：dims = " + dims);
            dimArr = dims.split(",");

            // 如果是 Kylin 查询 处理 纬度字符串
            if (fileUrl.startsWith("select ")) {
                for (int i = 0; i < dimArr.length; i++) {
                    if (dimArr[i].split("\\.").length == 3) {
                        dimArr[i] = dimArr[i].split("\\.")[1] + "." + dimArr[i].split("\\.")[2];
                    } else if (dimArr[i].split("\\.").length == 2) { //GD_GENERAL_INFO_W.EXAMINE_DATE_DAY
                        dimArr[i] = dimArr[i];
                    }
                }
            }

        }

        // 处理度量字符串为度量数组
        if (!StringUtil.isEmpty(meas)) {
            meas = StringUtil.custom_trim(meas, ',');
            System.out.println("--------去,后：meas = " + meas);
            funAndMeaArr = meas.split(",");
            for (String item : funAndMeaArr) {
                item = StringUtil.custom_trim(item, '.'); //去除首尾'.'
                String[] itemSplit = item.split("\\.");
                funArr.add(itemSplit[0]);
                if (itemSplit.length == 4) {         //操作名.数据库名.表名.维度
                    meaArr.add(itemSplit[2] + "." + itemSplit[3]);
                } else if (itemSplit.length == 2) {   //操作名.维度
                    meaArr.add(itemSplit[1]);
                } else if (itemSplit.length == 3) {   //操作名.表名.维度
                    meaArr.add(itemSplit[1] + "." + itemSplit[2]);
                }
            }
        }

        // 处理 行字符串 为 行数组
        if (!StringUtil.isEmpty(rows)) {
            rowArr = rows.split(",");
        }

        // 路由 获取计算引擎
        String routeStr = queryRoute.route(Arrays.asList(dimArr), funArr, meaArr, tableName, fileUrl);
        System.out.println("------------查询引擎是：" + routeStr + "-------------");

        //hive 查询出来列名都变成小写了
        if (routeStr.equals("hive")) {
            for (int i = 0; i < dimArr.length; i++) {
                dimArr[i] = dimArr[i].toLowerCase();
            }
            for (int i = 0; i < meaArr.size(); i++) {
                meaArr.set(i, meaArr.get(i).toLowerCase());
            }
        }

        // 获取SQL
        SQLGenerate sqlGenerate = new SQLGenerate();
        String sql;
        String drillFileNameJudge = "-1";
        String drillpath = DRILLPATH;
        if (meaArr.size() > 0 && funArr.size() > 0 && dimArr.length == 1)
            drillFileNameJudge = tableName + "-" + meaArr.get(0) + "_" + funArr.get(0) + "-" + dimArr[0];
        // 存储计算结果
        List<Map> listJson = new ArrayList<>();
        // 判断是否是上卷下钻
        System.out.println("Judge:::");
        System.out.println(!FileOperate.initDrillJudge(drillpath, drillFileNameJudge));
        System.out.println(rows != null);
        if (!FileOperate.initDrillJudge(drillpath, drillFileNameJudge) ||
                (rows != null && !rows.equals("")) ||           //多维表格模式
                (meaArr.size() > 1 && dimArr.length == 1) ||    //雷达图模式
                (meaArr.size() == 1 && dimArr.length == 0)      //指标卡模式
                ) {     //不是上卷下钻或目录中无之前的文件
            System.out.println("----------------非上卷下钻的操作----------------------");
            if (meaArr.size() == 1 && dimArr.length == 0)     //兼容指标卡的特殊Option
                sql = sqlGenerate.getWithOnemeas(funArr, meaArr, tableName, fileType, fileUrl, routeStr);
            else
                sql = sqlGenerate.getWithGroup(dimArr, funArr, meaArr, tableName, fileType, fileUrl, routeStr, limit);
            diagramSql.setSqlinfo(sql);
            System.out.println("The SQL is: " + sql);
            listJson = queryService.getQueryData(Arrays.asList(dimArr), funArr, meaArr, fileUrl, tableName, sql, routeStr);
        } else if (dimArr.length == 1) {
            System.out.println("----------------进入了上卷下钻的操作----------------------");
            sql = sqlGenerate.getWithScrollDrill(drillFileNameJudge, meas.split("\\."), -1, -1, -1, -1);
            diagramSql.setSqlinfo(sql);
            System.out.println("The SQL is: " + sql);
            listJson = queryService.getQueryDataWithDate(drillpath + drillFileNameJudge, drillFileNameJudge, sql);
        }

        Boolean drillflag = false;
        if (listJson.size() != 0 && !fileUrl.contains("select"))
            if (!listJson.get(0).containsKey(dims) && !(dimArr.length == 0 && meaArr.size() != 0) && !(dimArr.length > 1 && meaArr.size() == 1) && dimArr.length == 1)  //用来判断是否是可以上卷下钻的
                drillflag = true;
        if (drillflag) {                             // 为上卷下钻排序并增加一个"年"的后缀
            Collections.sort(listJson, new Comparator<Map>() {  //给整个listJson进行排序
                public int compare(Map o1, Map o2) {
                    Integer date1 = Integer.valueOf(o1.get("year").toString());
                    Integer date2 = Integer.valueOf(o2.get("year").toString());
                    return date1.compareTo(date2);
                }
            });
            for (Map tempmap : listJson) {
                tempmap.put("year", tempmap.get("year").toString() + "年");
            }
        }

        // 生成图的类型
        String clas = "";
        if (dimArr.length == 0) {
            clas = "-2"; //指标卡类型
            diagramSql.setChartType(-2);
        }
        if (dimArr.length == 1 && meaArr.size() > 1) {
            clas = "4"; //雷达图
            diagramSql.setChartType(4);
        } else if (dimArr.length == 1 && meaArr.size() == 1) {
            clas = "2"; //面积图
            diagramSql.setChartType(2);
        } else if (dimArr.length == 1 && meaArr.size() == 0) {
            clas = "-1"; //只有横轴的半成品图
            diagramSql.setChartType(-1);
        }
        if (dimArr.length > 1) {
            clas = "-3"; //数据表格类型
            diagramSql.setChartType(-3);
        }

        JSONObject re = new JSONObject();
        Diagram diagram = new Diagram();

        if (rowArr.length < 1) {       //返回 option
            List<String> mea_fun = new ArrayList<>();
            for (int i = 0; i < meaArr.size(); i++) {
                mea_fun.add(meaArr.get(i) + "_" + funArr.get(i));
            }
            JSONObject op = newOptionService.newCreateOption(dimArr, mea_fun, listJson);
            diagram = diagramService.createDiagram("-1", "picture", op.toString(), clas, userId, dataSourceId);
            re.put("option", op);

        } else if (rowArr.length > 0) {  //返回数据表格
            diagram = diagramService.createDiagram("-1", "picture", listJson.toString(), clas, userId, dataSourceId);
            //整理数据格式
            //构造列结构
            com.alibaba.fastjson.JSONArray cowJson = new GenerateTable().generateCowJSON(dimArr, rowArr, listJson);
            com.alibaba.fastjson.JSONArray rowJson = new GenerateTable().generateRowJSON(dimArr, meaArr, funArr, rowArr, listJson);
            com.alibaba.fastjson.JSONObject op = new com.alibaba.fastjson.JSONObject();
            op.put("row", rows);
            op.put("cows", cowJson);
            op.put("rows", rowJson);
            re.put("option", op);
        }
        diagramSql.setDiagramid(diagram.getId());
        diagramSQLRepository.saveAndFlush(diagramSql);
        re.put("diagramId", diagram.getId());
        re.put("diagramName", diagram.getName());
        re.put("classificaion", diagram.getClassification());
        re.put("userId", diagram.getUserId());
        re.put("dataSourceId", diagram.getDataSourceId());
        re.put("drillflag", drillflag);

        // 返回结果
        JSONObject result = new JSONObject();
        result.put("result", WebConstant.QUERY_SUCCESS.isResult());
        result.put("reason", WebConstant.QUERY_SUCCESS.getReason());
        result.put("datum", re);
        return result.toString();
    }


    /**
     * 图表转换接口
     *
     * @param diagramId
     * @param diagramName
     * @param diagramType
     * @param userId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/newupdateDiagram")
    public String newUpdateDiagram(int diagramId, String diagramName, int diagramType, int userId, HttpServletRequest request, HttpServletResponse response) {
        // 解决Ajax跨域请求问题
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("----------------newUpdateDiagram-----------");
        System.out.println("diagramId = " + diagramId + ";diagramName = " + diagramId + ";diagramType = " + diagramType + ";userID = " + userId);

        Optional<Diagram> diagram = diagramRepository.findById(Long.valueOf(diagramId));
        JSONObject result = new JSONObject();
        result.put("result", true);
        result.put("reson", ""); //TODO：错误拼写
        // 判断是否存在，新增/更新
        Diagram newDiagram;
        if (diagram.isPresent()) {
            newDiagram = diagram.get();
        } else {
            result.put("result", false);
            result.put("reson", "No such option");
            return result.toString();
        }
        // 获取option
        String ch = newDiagram.getChart();
        System.out.println(ch);
        JSONObject chOption = new JSONObject(ch);
        // 获取转换前option的类型
        int int_typeBefore = new chartsBase().getOptionType(chOption);
        System.out.println("typeBefore: " + int_typeBefore);
        System.out.println("typeAfter: " + diagramType);
        // option 转换
        String str_newDiagram = new chartsBase().transDiagram(int_typeBefore, diagramType, ch);

        diagramService.updateDiagram(diagramId + "", diagramName, str_newDiagram, "5", userId + "");

        //组datum对象
        JSONObject datum = new JSONObject();
        datum.put("option", new JSONObject(str_newDiagram));
        datum.put("diagramId", diagramId);
        datum.put("diagramName", diagramName);
        datum.put("userId", userId);
        result.put("datum", datum);

        return result.toString();
    }

    /**
     * 上钻下钻接口
     *
     * @param userId
     * @param dataSourceId
     * @param dim
     * @param mea
     * @param year
     * @param month
     * @param day
     * @param season
     * @param chartType
     * @param tableName
     * @param response
     * @param request
     * @return
     */
    @RequestMapping("/DataScrollDrill")
    public String dataScrollDrill(String userId, String dataSourceId, String dim, String mea, int year, int month, int day, int season, int chartType,
                                  String tableName, HttpServletResponse response, HttpServletRequest request) {

        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("----------------dataScrollDrill-----------");
        System.out.println("year = " + year + ", season = " + season + ", month = " + month + ", day = " + day);
        JSONObject result = new JSONObject();

        String[] measArr = mea.split("\\.");
        String[] dimArr = dim.split(",");
        for (int i = 0; i < dimArr.length; i++) {
            if (dimArr[i].split("\\.").length == 3) {
                dimArr[i] = dimArr[i].split("\\.")[1] + "." + dimArr[i].split("\\.")[2];
            } else if (dimArr[i].split("\\.").length == 2) {
                dimArr[i] = dimArr[i];
            }
        }

        String fileName = tableName + "-" + measArr[1] + "_" + measArr[0] + "-" + dimArr[0];

        SQLGenerate sqlGenerate = new SQLGenerate();
        String pathUrl = DRILLPATH;
        String sql = sqlGenerate.getWithScrollDrill(fileName, measArr, year, season, month, day);
        System.out.println("The SQL is : " + sql);
        List<Map> listJson = queryService.getQueryDataWithDate(pathUrl + fileName, fileName, sql);

        //整理一下最后的list
        final String colName = StringUtil.getcolname(listJson);
        String colNameInCN = "";
        switch (colName) {
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
        for (Map tempmap : listJson) {
            tempmap.put(colName, tempmap.get(colName).toString() + colNameInCN);
        }
        //整理listJson结束

        JSONObject re = new JSONObject();
        List<String> mea_fun = new ArrayList<>();
        mea_fun.add(measArr[1] + "_" + measArr[0]);
        JSONObject jo = newOptionService.newCreateOption(dimArr, mea_fun, listJson);
        Diagram diagram = diagramService.createDiagram("-1", "picture", jo.toString(), "2", userId, dataSourceId);
        String str_newDiagram = new chartsBase().transDiagram(2, chartType, diagram.getChart());
        diagramService.updateDiagram(diagram.getId() + "", diagram.getName(), str_newDiagram, "5", userId + "");

        re.put("option", new JSONObject(str_newDiagram));
        re.put("diagramId", diagram.getId());
        re.put("diagramName", diagram.getName());
        re.put("classificaion", diagram.getClassification());
        re.put("userId", diagram.getUserId());
        re.put("year", year);
        re.put("month", month);
        re.put("day", day);
        re.put("dataSourceId", diagram.getDataSourceId());
        re.put("drillflag", true);

        result.put("result", WebConstant.QUERY_SUCCESS.isResult());
        result.put("reason", WebConstant.QUERY_SUCCESS.getReason());
        result.put("datum", re);
        return result.toString();
    }
}
